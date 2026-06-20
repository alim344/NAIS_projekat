package com.example.practical_class_es.repo;

import com.example.practical_class_es.doc.PracticalClassLog;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface PracticalClassLogRepository extends ElasticsearchRepository<PracticalClassLog, String> {


}
