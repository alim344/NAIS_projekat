package com.example.practical_class_es.repo;

import com.example.practical_class_es.doc.CandidateAnalytics;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface CandidateAnalyticsRepository extends ElasticsearchRepository<CandidateAnalytics, String> {
}
