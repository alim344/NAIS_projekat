import os
import io
import requests
import streamlit as st
import pandas as pd
import plotly.express as px
import plotly.graph_objects as go
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
matplotlib.rcParams["font.family"] = "DejaVu Sans"  # supports š č ć ž đ
from datetime import datetime

from reportlab.lib.pagesizes import A4
from reportlab.lib.units import cm
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.lib import colors
from reportlab.platypus import (
    SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle, Image, PageBreak
)
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont

# ReportLab's built-in fonts (Helvetica etc.) have no glyphs for š/č/ć/ž/đ —
# they render as solid black boxes. Register a Unicode-capable TTF font and
# fall back gracefully if none is found on the host, so the app never
# crashes when the report is generated.
_FONT_CANDIDATES = [
    "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf",
    "/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf",
    "/usr/share/fonts/truetype/liberation/LiberationSans-Regular.ttf",
    "/usr/share/fonts/truetype/liberation/LiberationSans-Bold.ttf",
]


def _register_unicode_font():
    regular_path = next((p for p in _FONT_CANDIDATES if "Bold" not in p and os.path.exists(p)), None)
    bold_path = next((p for p in _FONT_CANDIDATES if "Bold" in p and os.path.exists(p)), None)
    if regular_path:
        pdfmetrics.registerFont(TTFont("ReportFont", regular_path))
        pdfmetrics.registerFont(TTFont("ReportFont-Bold", bold_path or regular_path))
        return "ReportFont", "ReportFont-Bold"
    # No Unicode TTF found on this host — fall back to a built-in font.
    # Diacritics (š č ć ž đ) won't render correctly in this case.
    return "Helvetica", "Helvetica-Bold"


FONT_REGULAR, FONT_BOLD = _register_unicode_font()

NEO4J_URL = os.getenv("NEO4J_URL", "http://localhost:8090")
ES_URL    = os.getenv("ES_URL",    "http://localhost:8092")

st.set_page_config(page_title="Theory Class Report", layout="wide")


def api_get(base_url, path, params=None):
    url = f"{base_url}{path}"
    try:
        r = requests.get(url, params=params, timeout=8)
        r.raise_for_status()
        return r.json(), None
    except requests.exceptions.ConnectionError:
        return None, f"Could not connect to {url}"
    except requests.exceptions.Timeout:
        return None, f"Request timed out: {url}"
    except requests.exceptions.HTTPError as e:
        return None, f"HTTP {e.response.status_code}: {url}"
    except Exception as e:
        return None, str(e)


def parse_iso_datetime(value):
    """Validate an ISO-datetime string typed by the user.
    Returns (parsed_value_or_None, error_message_or_None)."""
    value = value.strip()
    if not value:
        return None, None
    try:
        datetime.fromisoformat(value)
        return value, None
    except ValueError:
        return None, (
            f"'{value}' nije validan ISO datum/vreme. "
            "Očekivani format: YYYY-MM-DDTHH:MM:SS (npr. 2024-05-10T00:00:00)."
        )


# ─── sidebar ──────────────────────────────────────────────────────────────

with st.sidebar:
    st.markdown("**Theory Class Report**")
    st.caption(f"Neo4j: {NEO4J_URL}")
    st.caption(f"ES: {ES_URL}")
    st.divider()

    st.markdown("**Filteri — Sekcija 2**")
    professor_filter = st.text_input("Korisničko ime profesora")
    classroom_filter = st.text_input("Naziv učionice")

    st.divider()
    st.markdown("**Filteri — Sekcija 3**")
    from_date_raw = st.text_input("Od (ISO datum/vreme)", placeholder="2024-05-10T00:00:00")
    to_date_raw   = st.text_input("Do (ISO datum/vreme)", placeholder="2024-05-12T00:00:00")
    min_cand      = st.number_input("Min. broj kandidata", min_value=0, value=0)

    st.divider()
    if st.button("Osveži"):
        st.cache_data.clear()
        st.rerun()


st.title("Theory Class Report")
st.caption("Izvori podataka: Neo4j (theory-organization-service) i Elasticsearch (theory-search-service)")
st.divider()


# ─── Section 1 — Scheduled classes (Neo4j) ───────────────────────────────────

st.subheader("1. Zakazani teorijski časovi")
st.caption("Izvor: Neo4j / theory-organization-service :8090")

classes_data, err = api_get(NEO4J_URL, "/api/theory-classes")
df1 = pd.DataFrame()
section1_metrics = {}

if err:
    st.error(err)
else:
    classes = classes_data if isinstance(classes_data, list) else classes_data.get("content", [])

    total = len(classes)
    capacity = sum((c.get("hall") or {}).get("capacity", 0) for c in classes)
    section1_metrics = {
        "Zakazani časovi": total,
        "Ukupan kapacitet": capacity,
    }

    c1, c2 = st.columns(2)
    c1.metric("Zakazani časovi", total)
    c2.metric("Ukupan kapacitet", capacity)

    st.markdown("")

    rows = []
    for c in classes:
        dt_raw = c.get("startTime", "")
        try:
            dt = datetime.fromisoformat(dt_raw).strftime("%Y-%m-%d %H:%M")
        except Exception:
            dt = dt_raw
        lesson = c.get("theoryLesson") or {}
        room   = c.get("hall") or {}
        cap    = room.get("capacity", 0)
        rows.append({
            "ID": c.get("id"),
            "Datum i vreme": dt,
            "Lekcija": f"L{lesson.get('orderNumber','?')} - {lesson.get('title','')}",
            "Učionica": room.get("name", "—"),
            "Kapacitet": cap,
        })

    df1 = pd.DataFrame(rows)

    st.dataframe(
        df1,
        use_container_width=True,
        hide_index=True,
    )
    st.caption(f"{len(df1)} zapisa")


st.divider()


# ─── Section 2 — Class logs (Elasticsearch) ──────────────────────────────────

st.subheader("2. Evidencija održanih časova")
st.caption("Izvor: Elasticsearch / theory-search-service :8092")

if professor_filter.strip():
    logs_data, err2 = api_get(ES_URL, f"/api/class-logs/by-professor/{professor_filter.strip()}")
elif classroom_filter.strip():
    logs_data, err2 = api_get(ES_URL, f"/api/class-logs/by-classroom/{classroom_filter.strip()}")
else:
    logs_data, err2 = api_get(ES_URL, "/api/class-logs")

df2 = pd.DataFrame()
section2_metrics = {}

if err2:
    st.error(err2)
else:
    logs = logs_data if isinstance(logs_data, list) else []

    total_pres = sum(l.get("candidateCount", 0) for l in logs)
    fully_attended_count = sum(1 for l in logs if l.get("fullyAttended"))
    avg_dur    = round(sum(l.get("durationMinutes", 0) for l in logs) / len(logs)) if logs else 0
    section2_metrics = {
        "Broj zapisa": len(logs),
        "Prisutni (ukupno)": total_pres,
        "Časovi sa punom popunjenošću": fully_attended_count,
        "Prosečno trajanje": f"{avg_dur} min",
    }

    c1, c2, c3, c4 = st.columns(4)
    c1.metric("Broj zapisa", len(logs))
    c2.metric("Prisutni (ukupno)", total_pres)
    c3.metric("Časovi sa punom popunjenošću", fully_attended_count)
    c4.metric("Prosečno trajanje", f"{avg_dur} min")

    st.markdown("")

    log_rows = []
    for l in logs:
        dt_raw = l.get("startTime", "")
        try:
            dt = datetime.fromisoformat(dt_raw).strftime("%Y-%m-%d %H:%M")
        except Exception:
            dt = dt_raw
        pres = l.get("candidateCount", 0)
        log_rows.append({
            "ID": l.get("id", "")[:8],
            "Predavač": l.get("professorUsername", ""),
            "Učionica": l.get("classroomName", ""),
            "Kategorija": l.get("category", ""),
            "Početak": dt,
            "Trajanje (min)": l.get("durationMinutes", 0),
            "Prisutno": pres,
            "Kapacitet": l.get("classroomCapacity", 0),
            "Puna popunjenost": "Da" if l.get("fullyAttended") else "Ne",
        })

    df2 = pd.DataFrame(log_rows)

    if not df2.empty:
        def color_full(val):
            return "background-color: #d4edda" if val == "Da" else "background-color: #f8d7da"

        st.dataframe(
            df2.style.map(color_full, subset=["Puna popunjenost"]),
            use_container_width=True,
            hide_index=True,
        )
    else:
        st.dataframe(df2, use_container_width=True, hide_index=True)

    st.caption(f"{len(df2)} zapisa  |  zeleno = puna popunjenost  |  crveno = nepuna popunjenost")


st.divider()


# ─── Section 3 — Complex analysis (Neo4j + ES) ───────────────────────────────

st.subheader("3. Analiza po predavačima i učionicama (kompleksna sekcija)")
st.caption("Izvor: Elasticsearch / theory-search-service :8092")
st.markdown(
    "Kombinuje statistiku po predavaču (`analyzeClassesByDateAndProfessor`) "
    "sa statistikom po učionici (`analyzeClassroomUsage`)."
)

from_date, from_date_err = parse_iso_datetime(from_date_raw)
to_date, to_date_err = parse_iso_datetime(to_date_raw)

if from_date_err:
    st.warning(from_date_err)
if to_date_err:
    st.warning(to_date_err)

params = {}
if from_date: params["fromDate"] = from_date
if to_date:   params["toDate"]   = to_date
if min_cand > 0: params["minCandidates"] = min_cand
if professor_filter.strip(): params["professorUsername"] = professor_filter.strip()

analysis_data, err3 = (None, None)
classroom_data, err4 = (None, None)

# Don't fire the query with a malformed date — avoids an opaque backend error.
if from_date_err or to_date_err:
    err3 = "Ispravite datume u filterima da bi se učitala analiza."
else:
    analysis_data, err3 = api_get(ES_URL, "/api/class-logs/analyze-by-professor", params=params or None)
    classroom_data, err4 = api_get(ES_URL, "/api/class-logs/analyze-classroom-usage", params=params or None)

if err3: st.error(err3)
if err4: st.error(err4)


def flatten_keyed_stats(entries):
    """statsByProfessor / statsByClassroom come back as a list of single-key
    objects, e.g. [{"prof": {...}}, {"prof2": {...}}] — Jackson's default
    serialization of a Map.Entry stream. Flatten to [(key, stats), ...]."""
    flattened = []
    for entry in entries or []:
        for key, stats in entry.items():
            flattened.append((key, stats))
    return flattened


df3 = pd.DataFrame()
cu_rows = []
section3_metrics = {}

if not err3 and analysis_data:
    section3_metrics = {
        "Ukupno časova (filtrirano)": analysis_data.get("totalHits", "-"),
        "Ukupno kandidata prisutno": analysis_data.get("totalCandidatesAttended", "-"),
        "Prosečno trajanje (min)": round(analysis_data.get("overallAverageDuration", 0), 1),
        "Prosečan rezultat kandidata": round(analysis_data.get("overallAverageScore", 0), 1),
    }

    c1, c2, c3, c4 = st.columns(4)
    c1.metric("Ukupno časova (filtrirano)", section3_metrics["Ukupno časova (filtrirano)"])
    c2.metric("Ukupno kandidata prisutno", section3_metrics["Ukupno kandidata prisutno"])
    c3.metric("Prosečno trajanje (min)", section3_metrics["Prosečno trajanje (min)"])
    c4.metric("Prosečan rezultat kandidata", section3_metrics["Prosečan rezultat kandidata"])

    st.markdown("")
    st.markdown("**Po predavaču**")

    prof_rows = []
    for username, stats in flatten_keyed_stats(analysis_data.get("statsByProfessor")):
        prof_rows.append({
            "Predavač": stats.get("fullName", username),
            "Broj časova": stats.get("classCount", 0),
            "Ukupno kandidata": stats.get("totalCandidates", 0),
            "Prosečno kandidata/času": round(stats.get("averageCandidatesPerClass", 0), 1),
            "Prosečno trajanje (min)": round(stats.get("averageDurationMinutes", 0), 1),
            "Prosečan rezultat": round(stats.get("averageScore", 0), 1),
        })

    df3 = pd.DataFrame(prof_rows)

    st.dataframe(
        df3,
        use_container_width=True,
        hide_index=True,
        column_config={
            "Prosečan rezultat": st.column_config.ProgressColumn(
                "Prosečan rezultat", min_value=0, max_value=100, format="%.1f"
            )
        },
    )

if not err4 and classroom_data:
    st.markdown("**Popunjenost učionica**")
    for name, stats in flatten_keyed_stats(classroom_data.get("statsByClassroom")):
        cu_rows.append({
            "Učionica": name,
            "Ukupno časova": stats.get("totalClasses", 0),
            "Prosečna popunjenost %": round(stats.get("averageOccupancyPercent", 0), 1),
            "Kapacitet": stats.get("capacity", 0),
        })
    if cu_rows:
        st.dataframe(pd.DataFrame(cu_rows), use_container_width=True, hide_index=True)


st.divider()


# ─── Section 4 — Charts ───────────────────────────────────────────────────────

st.subheader("4. Grafikoni")
st.caption("Izvor: Elasticsearch / theory-search-service :8092")

if (not err3 and analysis_data) or (not err4 and classroom_data):
    col1, col2 = st.columns(2)

    with col1:
        if cu_rows:
            fig1 = go.Figure()
            fig1.add_trace(go.Bar(
                x=[c["Učionica"] for c in cu_rows],
                y=[c["Prosečna popunjenost %"] for c in cu_rows],
                name="Popunjenost (%)",
                marker_color="#4C72B0",
                text=[f"{c['Prosečna popunjenost %']:.1f}%" for c in cu_rows],
                textposition="outside",
            ))
            fig1.add_trace(go.Scatter(
                x=[c["Učionica"] for c in cu_rows],
                y=[c["Ukupno časova"] for c in cu_rows],
                name="Časovi",
                yaxis="y2",
                mode="lines+markers",
                line=dict(color="#DD8452", width=2),
            ))
            fig1.add_hline(y=100, line_dash="dash", line_color="gray",
                           annotation_text="Puni kapacitet")
            max_classes = max((c["Ukupno časova"] for c in cu_rows), default=0)
            fig1.update_layout(
                title="Popunjenost učionica",
                yaxis=dict(title="Popunjenost (%)", range=[0, 120]),
                yaxis2=dict(title="Časovi", overlaying="y", side="right",
                            range=[0, max_classes * 2 + 1]),
                legend=dict(orientation="h", y=-0.2),
                height=360,
            )
            st.plotly_chart(fig1, use_container_width=True)
        else:
            st.info("Nema podataka o popunjenosti učionica za prikaz grafikona.")

    with col2:
        if not df3.empty:
            fig2 = px.bar(
                x=df3["Prosečan rezultat"],
                y=df3["Predavač"],
                orientation="h",
                text=[f"{v:.1f}" for v in df3["Prosečan rezultat"]],
                title="Prosečan rezultat kandidata po predavaču",
            )
            fig2.update_layout(
                xaxis=dict(title="Prosečan rezultat", range=[0, 110]),
                yaxis=dict(title=""),
                height=360,
                showlegend=False,
            )
            st.plotly_chart(fig2, use_container_width=True)
        else:
            st.info("Nema podataka po predavaču za prikaz grafikona.")

    if not df2.empty and "Kategorija" in df2.columns:
        cat_counts = df2["Kategorija"].value_counts().reset_index()
        cat_counts.columns = ["Kategorija", "Broj"]
        fig3 = px.pie(cat_counts, names="Kategorija", values="Broj",
                      title="Časovi po kategoriji", hole=0.3)
        fig3.update_layout(height=340)
        st.plotly_chart(fig3, use_container_width=True)

else:
    st.warning("Grafikoni nisu dostupni — podaci za analizu nisu mogli biti učitani.")


st.divider()


# ─── Section 5 — PDF report export ───────────────────────────────────────────

st.subheader("5. Preuzimanje izveštaja")
st.caption("Generiše PDF dokument sa podacima trenutno prikazanim u izveštaju iznad.")


def build_classroom_chart_image(cu_rows):
    """Render the classroom-occupancy chart with matplotlib so it can be
    embedded in the PDF (Plotly figures can't be embedded directly)."""
    if not cu_rows:
        return None
    names = [r["Učionica"] for r in cu_rows]
    occ   = [r["Prosečna popunjenost %"] for r in cu_rows]

    fig, ax = plt.subplots(figsize=(6.2, 3.2))
    bars = ax.bar(names, occ, color="#4C72B0")
    ax.axhline(100, color="gray", linestyle="--", linewidth=1)
    ax.set_ylabel("Popunjenost (%)")
    ax.set_title("Prosečna popunjenost učionica")
    ax.set_ylim(0, max(120, (max(occ) if occ else 0) + 20))
    for bar, val in zip(bars, occ):
        ax.text(bar.get_x() + bar.get_width() / 2, val + 2, f"{val:.1f}%",
                 ha="center", va="bottom", fontsize=8)
    plt.xticks(rotation=20, ha="right", fontsize=8)
    plt.tight_layout()

    buf = io.BytesIO()
    fig.savefig(buf, format="png", dpi=150)
    plt.close(fig)
    buf.seek(0)
    return buf


def df_to_table(df, max_rows=25):
    """Convert a DataFrame into a reportlab Table, truncating long tables."""
    if df is None or df.empty:
        return None
    shown = df.head(max_rows)
    data = [list(shown.columns)] + shown.astype(str).values.tolist()
    table = Table(data, repeatRows=1)
    table.setStyle(TableStyle([
        ("FONTNAME", (0, 0), (-1, -1), FONT_REGULAR),
        ("FONTNAME", (0, 0), (-1, 0), FONT_BOLD),
        ("BACKGROUND", (0, 0), (-1, 0), colors.HexColor("#4C72B0")),
        ("TEXTCOLOR", (0, 0), (-1, 0), colors.white),
        ("FONTSIZE", (0, 0), (-1, -1), 7),
        ("ROWBACKGROUNDS", (0, 1), (-1, -1), [colors.white, colors.HexColor("#f2f2f2")]),
        ("GRID", (0, 0), (-1, -1), 0.4, colors.HexColor("#cccccc")),
        ("VALIGN", (0, 0), (-1, -1), "MIDDLE"),
        ("LEFTPADDING", (0, 0), (-1, -1), 4),
        ("RIGHTPADDING", (0, 0), (-1, -1), 4),
    ]))
    return table


def metrics_table(metrics: dict):
    if not metrics:
        return None
    data = [[k, str(v)] for k, v in metrics.items()]
    table = Table(data, colWidths=[7 * cm, 7 * cm])
    table.setStyle(TableStyle([
        ("FONTNAME", (0, 0), (-1, -1), FONT_REGULAR),
        ("FONTSIZE", (0, 0), (-1, -1), 9),
        ("FONTNAME", (0, 0), (0, -1), FONT_BOLD),
        ("GRID", (0, 0), (-1, -1), 0.3, colors.HexColor("#dddddd")),
        ("BACKGROUND", (0, 0), (0, -1), colors.HexColor("#eef2f8")),
        ("LEFTPADDING", (0, 0), (-1, -1), 6),
        ("TOPPADDING", (0, 0), (-1, -1), 4),
        ("BOTTOMPADDING", (0, 0), (-1, -1), 4),
    ]))
    return table


def generate_pdf_report():
    buf = io.BytesIO()
    doc = SimpleDocTemplate(
        buf, pagesize=A4,
        topMargin=1.5 * cm, bottomMargin=1.5 * cm,
        leftMargin=1.5 * cm, rightMargin=1.5 * cm,
    )
    styles = getSampleStyleSheet()
    h1 = ParagraphStyle("h1", parent=styles["Title"], fontName=FONT_BOLD)
    h2 = ParagraphStyle("h2", parent=styles["Heading2"], fontName=FONT_BOLD, spaceBefore=14, spaceAfter=6)
    h3 = ParagraphStyle("h3", parent=styles["Heading3"], fontName=FONT_BOLD)
    normal = ParagraphStyle("normal", parent=styles["Normal"], fontName=FONT_REGULAR)
    caption = ParagraphStyle("caption", parent=styles["Normal"], fontName=FONT_REGULAR,
                              fontSize=8, textColor=colors.grey)

    story = []
    story.append(Paragraph("Theory Class Report", h1))
    story.append(Paragraph(
        f"Generisano: {datetime.now().strftime('%Y-%m-%d %H:%M')}", caption))
    story.append(Paragraph(
        "Izvori podataka: Neo4j (theory-organization-service) i "
        "Elasticsearch (theory-search-service).", normal))
    story.append(Spacer(1, 10))

    # Section 1 — simple
    story.append(Paragraph("1. Zakazani teorijski časovi", h2))
    story.append(Paragraph("Izvor: Neo4j / theory-organization-service :8090", caption))
    if section1_metrics:
        story.append(Spacer(1, 4))
        story.append(metrics_table(section1_metrics))
    story.append(Spacer(1, 6))
    t1 = df_to_table(df1)
    if t1:
        story.append(t1)
        if len(df1) > 25:
            story.append(Paragraph(f"Prikazano 25 od {len(df1)} zapisa.", caption))
    else:
        story.append(Paragraph("Nema podataka za prikaz.", normal))

    story.append(PageBreak())

    # Section 2 — simple
    story.append(Paragraph("2. Evidencija održanih časova", h2))
    story.append(Paragraph("Izvor: Elasticsearch / theory-search-service :8092", caption))
    if section2_metrics:
        story.append(Spacer(1, 4))
        story.append(metrics_table(section2_metrics))
    story.append(Spacer(1, 6))
    t2 = df_to_table(df2)
    if t2:
        story.append(t2)
        if len(df2) > 25:
            story.append(Paragraph(f"Prikazano 25 od {len(df2)} zapisa.", caption))
    else:
        story.append(Paragraph("Nema podataka za prikaz.", normal))

    story.append(PageBreak())

    # Section 3 — complex
    story.append(Paragraph("3. Analiza po predavačima i učionicama (kompleksna sekcija)", h2))
    story.append(Paragraph(
        "Kombinuje statistiku po predavaču (analyzeClassesByDateAndProfessor) "
        "sa statistikom po učionici (analyzeClassroomUsage).", caption))
    if section3_metrics:
        story.append(Spacer(1, 4))
        story.append(metrics_table(section3_metrics))
    story.append(Spacer(1, 6))
    t3 = df_to_table(df3)
    if t3:
        story.append(t3)
    else:
        story.append(Paragraph("Nema podataka za prikaz.", normal))

    if cu_rows:
        story.append(Spacer(1, 10))
        story.append(Paragraph("Popunjenost učionica", h3))
        t_cu = df_to_table(pd.DataFrame(cu_rows))
        if t_cu:
            story.append(t_cu)

        chart_buf = build_classroom_chart_image(cu_rows)
        if chart_buf:
            story.append(Spacer(1, 10))
            story.append(Image(chart_buf, width=15 * cm, height=7.5 * cm))

    doc.build(story)
    buf.seek(0)
    return buf


can_generate = not (err and err2 and err3)

if can_generate:
    if st.button("📄 Generiši PDF izveštaj"):
        with st.spinner("Generisanje PDF izveštaja..."):
            pdf_buffer = generate_pdf_report()
        st.session_state["pdf_report"] = pdf_buffer.getvalue()

    if "pdf_report" in st.session_state:
        st.download_button(
            label="⬇️ Preuzmi izveštaj (PDF)",
            data=st.session_state["pdf_report"],
            file_name=f"theory_class_report_{datetime.now().strftime('%Y%m%d_%H%M')}.pdf",
            mime="application/pdf",
        )
else:
    st.warning("Nije moguće generisati izveštaj jer nijedan izvor podataka nije dostupan.")