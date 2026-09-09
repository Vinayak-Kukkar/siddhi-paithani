package com.siddhi.paithani.service.impl;

import com.siddhi.paithani.entity.ProductQuestion;
import com.siddhi.paithani.repository.ProductQuestionRepository;
import com.siddhi.paithani.service.ProductQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductQuestionServiceImpl implements ProductQuestionService {

    @Autowired
    private ProductQuestionRepository questionRepository;

    @Override
    public ProductQuestion askQuestion(Long productId, String customerName, String customerEmail, String question) {
        ProductQuestion q = new ProductQuestion(productId, customerName, customerEmail, question);
        return questionRepository.save(q);
    }

    @Override
    public ProductQuestion answerQuestion(Long questionId, String answer) {
        ProductQuestion q = questionRepository.findById(questionId).orElse(null);
        if (q != null) {
            q.setAnswer(answer);
            q.setAnswered(true);
            return questionRepository.save(q);
        }
        return null;
    }

    @Override
    public List<ProductQuestion> getQuestionsByProduct(Long productId) {
        return questionRepository.findByProductIdOrderByCreatedAtDesc(productId);
    }

    @Override
    public List<ProductQuestion> getAnsweredQuestionsByProduct(Long productId) {
        return questionRepository.findByProductIdAndIsAnsweredTrueOrderByCreatedAtDesc(productId);
    }

    @Override
    public List<ProductQuestion> getAllQuestions() {
        return questionRepository.findAllByOrderByCreatedAtDesc();
    }
}
