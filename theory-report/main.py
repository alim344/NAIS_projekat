import os
import io
import requests
import streamlit as st
import pandas as pd
import plotly.graph_objects as go
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
from datetime import datetime

from reportlab.lib.pagesizes import A4
from reportlab.lib.units import cm
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.lib import colors
from reportlab.platypus import SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle, Image, PageBreak
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont

def _register_font():
    candidates = [
        "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf",
        "/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf",
    ]
    reg  = next((p for p in candidates if "Bold" not in p and os.path.exists(p)), None)
    bold = next((p for p in candidates if "Bold" in p     and os.path.exists(p)), None)
    if reg:
        pdfmetrics.registerFont(TTFont("RF",  reg))
        pdfmetrics.registerFont(TTFont("RFB", bold or reg))
        return "RF", "RFB"
    return "Helvetica", "Helvetica-Bold"

FONT, FONTB = _register_font()

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
        return None, f"Timeout: {url}"
    except requests.exceptions.HTTPError as e:
        return None, f"HTTP {e.response.status_code}: {url}"
    except Exception as e:
        return None, str(e)


def flatten_keyed_stats(entries):
    result = []
    for entry in (entries or []):
        for key, stats in entry.items():
            result.append((key, stats))
    return result


# ── sidebar ───────────────────────────────────────────────────────────────────

with st.sidebar:
    st.markdown("**Theory Class Report**")
    st.caption(f"Neo4j: {NEO4J_URL}")
    st.caption(f"ES: {ES_URL}")
    st.divider()

    st.markdown("**Filters — Section 2**")
    professor_filter = st.text_input("Professor username")
    classroom_filter = st.text_input("Classroom name")

    st.divider()
    st.markdown("**Filters — Section 3**")
    from_date = st.text_input("From (ISO)", placeholder="2024-05-10T00:00:00")
    to_date   = st.text_input("To (ISO)",   placeholder="2024-05-12T00:00:00")
    min_cand  = st.number_input("Min. candidates", min_value=0, value=0)

    st.divider()
    if st.button("Refresh"):
        st.cache_data.clear()
        st.rerun()


st.title("Theory Class Report")
st.caption("Sources: Neo4j (theory-organization-service) and Elasticsearch (theory-search-service)")
st.divider()


# ── Section 1 — Scheduled classes (Neo4j) ────────────────────────────────────

st.subheader("1. Scheduled Theory Classes")
st.caption("Source: Neo4j / theory-organization-service :8090")

classes_data, err1 = api_get(NEO4J_URL, "/api/theory-classes")
df1 = pd.DataFrame()

if err1:
    st.error(err1)
else:
    classes = classes_data if isinstance(classes_data, list) else classes_data.get("content", [])

    rows = []
    for c in classes:
        raw = c.get("startTime", "")
        try:    dt = datetime.fromisoformat(raw).strftime("%Y-%m-%d %H:%M")
        except: dt = raw
        lesson = c.get("theoryLesson") or {}
        room   = c.get("hall") or {}
        rows.append({
            "ID":          c.get("id"),
            "Date & Time": dt,
            "Lesson":      f"L{lesson.get('orderNumber','?')} - {lesson.get('title','')}",
            "Classroom":   room.get("name", "—"),
            "Capacity":    room.get("capacity", 0),
        })

    df1 = pd.DataFrame(rows)
    c1, c2 = st.columns(2)
    c1.metric("Scheduled classes", len(classes))
    c2.metric("Total capacity", sum(r["Capacity"] for r in rows))
    st.dataframe(df1, use_container_width=True, hide_index=True)
    st.caption(f"{len(df1)} records")

st.divider()


# ── Section 2 — Class logs (Elasticsearch) ───────────────────────────────────

st.subheader("2. Class Log Records")
st.caption("Source: Elasticsearch / theory-search-service :8092")

if professor_filter.strip():
    logs_data, err2 = api_get(ES_URL, f"/api/class-logs/by-professor/{professor_filter.strip()}")
elif classroom_filter.strip():
    logs_data, err2 = api_get(ES_URL, f"/api/class-logs/by-classroom/{classroom_filter.strip()}")
else:
    logs_data, err2 = api_get(ES_URL, "/api/class-logs")

df2 = pd.DataFrame()

if err2:
    st.error(err2)
else:
    logs = logs_data if isinstance(logs_data, list) else []

    log_rows = []
    for l in logs:
        raw = l.get("startTime", "")
        try:    dt = datetime.fromisoformat(raw).strftime("%Y-%m-%d %H:%M")
        except: dt = raw
        log_rows.append({
            "ID":             l.get("id", "")[:8],
            "Instructor":     l.get("professorUsername", ""),
            "Classroom":      l.get("classroomName", ""),
            "Category":       l.get("category", ""),
            "Start Time":     dt,
            "Duration (min)": l.get("durationMinutes", 0),
            "Present":        l.get("candidateCount", 0),
            "Capacity":       l.get("classroomCapacity", 0),
            "Full":           "Yes" if l.get("fullyAttended") else "No",
        })

    df2 = pd.DataFrame(log_rows)

    total_present = sum(l.get("candidateCount", 0) for l in logs)
    full_count    = sum(1 for l in logs if l.get("fullyAttended"))
    #avg_dur       = round(sum(l.get("durationMinutes", 0) for l in logs) / len(logs)) if logs else 0

    c1, c2, c3, c4 = st.columns(4)
    c1.metric("Log records",    len(logs))
    c2.metric("Total present",  total_present)
    c3.metric("Fully attended", full_count)
    #c4.metric("Avg. duration",  f"{avg_dur} min")

    if not df2.empty:
        def color_full(val):
            return "background-color: #d4edda" if val == "Yes" else "background-color: #f8d7da"
        st.dataframe(
            df2.style.map(color_full, subset=["Full"]),
            use_container_width=True, hide_index=True,
        )
    else:
        st.info("No records found.")
    st.caption(f"{len(df2)} records")

st.divider()


# ── Section 3 — Complex analysis ─────────────────────────────────────────────

st.subheader("3. Instructor & Classroom Analysis")
st.caption("Source: Elasticsearch / theory-search-service :8092")

params = {}
if from_date.strip():        params["fromDate"]          = from_date.strip()
if to_date.strip():          params["toDate"]            = to_date.strip()
if min_cand > 0:             params["minCandidates"]     = min_cand
if professor_filter.strip(): params["professorUsername"] = professor_filter.strip()

analysis_data,  err3 = api_get(ES_URL, "/api/class-logs/analyze-by-professor",   params or None)
classroom_data, err4 = api_get(ES_URL, "/api/class-logs/analyze-classroom-usage", params or None)

if err3: st.error(err3)
if err4: st.error(err4)

df3     = pd.DataFrame()
cu_rows = []

if not err3 and analysis_data:
    prof_rows = []
    for username, stats in flatten_keyed_stats(analysis_data.get("statsByProfessor")):
        prof_rows.append({
            "Instructor":       stats.get("fullName", username),
            "Classes":          stats.get("classCount", 0),
            "Total candidates": stats.get("totalCandidates", 0),
           # "Avg. duration":    round(stats.get("averageDurationMinutes", 0), 1),
        })
    df3 = pd.DataFrame(prof_rows)
    if not df3.empty:
        st.markdown("**Per instructor**")
        st.dataframe(df3, use_container_width=True, hide_index=True)
    else:
        st.info("No instructor data returned.")

if not err4 and classroom_data:
    for name, stats in flatten_keyed_stats(classroom_data.get("statsByClassroom")):
        cu_rows.append({
            "Classroom":        name,
            "Total classes":    stats.get("totalClasses", 0),
            #"Avg. occupancy %": round(stats.get("averageOccupancyPercent", 0), 1),
            "Capacity":         stats.get("capacity", 0),
        })
    if cu_rows:
        st.markdown("**Classroom occupancy**")
        st.dataframe(pd.DataFrame(cu_rows), use_container_width=True, hide_index=True)
    else:
        st.info("No classroom data returned.")

st.divider()


# ── Section 4 — Chart ─────────────────────────────────────────────────────────

st.subheader("4. Chart — Instructor activity")
st.caption("Source: Elasticsearch / theory-search-service :8092")

if not df2.empty and "Instructor" in df2.columns:
    per_instructor = (
        df2.groupby("Instructor")
        .agg(Classes=("ID", "count"), Present=("Present", "sum"), Duration=("Duration (min)", "mean"))
        .reset_index()
        .sort_values("Classes", ascending=False)
    )

    fig = go.Figure()
    fig.add_trace(go.Bar(
        name="Classes held",
        x=per_instructor["Instructor"],
        y=per_instructor["Classes"],
        marker_color="#4C72B0",
        yaxis="y1",
    ))
    fig.add_trace(go.Bar(
        name="Total candidates present",
        x=per_instructor["Instructor"],
        y=per_instructor["Present"],
        marker_color="#55A868",
        yaxis="y1",
    ))
    #fig.add_trace(go.Scatter(
    #    name="Avg. duration (min)",
    #    x=per_instructor["Instructor"],
    #    y=per_instructor["Duration"].round(1),
    #    mode="lines+markers",
    #    marker=dict(size=8, color="#DD8452"),
    #    line=dict(color="#DD8452", width=2, dash="dot"),
    #    yaxis="y2",
    #))
    fig.update_layout(
        barmode="group",
        yaxis=dict(title="Count"),
        #yaxis2=dict(title="Avg. duration (min)", overlaying="y", side="right", showgrid=False),
        legend=dict(orientation="h", y=-0.2),
        height=420,
        margin=dict(t=30, b=80),
    )
    st.plotly_chart(fig, use_container_width=True)
else:
    st.info("No data for chart — Section 2 data not loaded.")

st.divider()


# ── Section 5 — PDF export ────────────────────────────────────────────────────

st.subheader("5. Download Report")

def make_table(df, max_rows=25):
    if df is None or df.empty:
        return None
    data = [list(df.columns)] + df.head(max_rows).astype(str).values.tolist()
    t = Table(data, repeatRows=1)
    t.setStyle(TableStyle([
        ("FONTNAME",       (0, 0), (-1, -1), FONT),
        ("FONTNAME",       (0, 0), (-1,  0), FONTB),
        ("BACKGROUND",     (0, 0), (-1,  0), colors.HexColor("#4C72B0")),
        ("TEXTCOLOR",      (0, 0), (-1,  0), colors.white),
        ("FONTSIZE",       (0, 0), (-1, -1), 7),
        ("ROWBACKGROUNDS", (0, 1), (-1, -1), [colors.white, colors.HexColor("#f2f2f2")]),
        ("GRID",           (0, 0), (-1, -1), 0.4, colors.HexColor("#cccccc")),
        ("LEFTPADDING",    (0, 0), (-1, -1), 4),
        ("RIGHTPADDING",   (0, 0), (-1, -1), 4),
    ]))
    return t


def make_chart_image():
    if df2.empty or "Instructor" not in df2.columns:
        return None
    per_instructor = (
        df2.groupby("Instructor")
        .agg(Classes=("ID", "count"), Present=("Present", "sum"))
        .reset_index()
        .sort_values("Classes", ascending=False)
    )
    instructors = per_instructor["Instructor"].tolist()
    x = range(len(instructors))
    width = 0.35
    fig, ax = plt.subplots(figsize=(max(6, len(instructors) * 1.2), 4))
    ax.bar([i - width/2 for i in x], per_instructor["Classes"], width, label="Classes held", color="#4C72B0")
    ax.bar([i + width/2 for i in x], per_instructor["Present"], width, label="Total candidates present", color="#55A868")
    ax.set_xticks(list(x))
    ax.set_xticklabels(instructors, rotation=20, ha="right")
    ax.set_ylabel("Count")
    ax.set_title("Instructor activity")
    ax.legend()
    plt.tight_layout()
    buf = io.BytesIO()
    fig.savefig(buf, format="png", dpi=150)
    plt.close(fig)
    buf.seek(0)
    return buf


def generate_pdf():
    buf = io.BytesIO()
    doc = SimpleDocTemplate(buf, pagesize=A4,
                            topMargin=1.5*cm, bottomMargin=1.5*cm,
                            leftMargin=1.5*cm, rightMargin=1.5*cm)
    styles = getSampleStyleSheet()
    h1  = ParagraphStyle("h1",  parent=styles["Title"],    fontName=FONTB)
    h2  = ParagraphStyle("h2",  parent=styles["Heading2"], fontName=FONTB, spaceBefore=14)
    cap = ParagraphStyle("cap", parent=styles["Normal"],   fontName=FONT, fontSize=8, textColor=colors.grey)
    nor = ParagraphStyle("nor", parent=styles["Normal"],   fontName=FONT)

    story = [
        Paragraph("Theory Class Report", h1),
        Paragraph(f"Generated: {datetime.now().strftime('%Y-%m-%d %H:%M')}", cap),
        Spacer(1, 10),
        Paragraph("1. Scheduled Theory Classes", h2),
        Paragraph("Source: Neo4j / theory-organization-service :8090", cap),
        Spacer(1, 6),
    ]
    story.append(make_table(df1) or Paragraph("No data.", nor))

    story += [
        PageBreak(),
        Paragraph("2. Class Log Records", h2),
        Paragraph("Source: Elasticsearch / theory-search-service :8092", cap),
        Spacer(1, 6),
    ]
    story.append(make_table(df2) or Paragraph("No data.", nor))

    story += [
        PageBreak(),
        Paragraph("3. Instructor & Classroom Analysis", h2),
        Paragraph("Source: Elasticsearch / theory-search-service :8092", cap),
        Spacer(1, 6),
    ]
    story.append(make_table(df3) or Paragraph("No data.", nor))
    if cu_rows:
        story += [Spacer(1, 10), Paragraph("Classroom occupancy", h2)]
        story.append(make_table(pd.DataFrame(cu_rows)) or Paragraph("No data.", nor))

    chart = make_chart_image()
    if chart:
        story += [
            PageBreak(),
            Paragraph("4. Chart — Classes per instructor", h2),
            Spacer(1, 6),
            Image(chart, width=15*cm, height=7.5*cm),
        ]

    doc.build(story)
    buf.seek(0)
    return buf


if st.button("Generate PDF"):
    with st.spinner("Generating..."):
        pdf = generate_pdf()
    st.download_button(
        label="Download PDF",
        data=pdf,
        file_name=f"theory_report_{datetime.now().strftime('%Y%m%d_%H%M')}.pdf",
        mime="application/pdf",
    )