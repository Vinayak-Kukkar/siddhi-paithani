package com.siddhi.paithani.service;

import com.siddhi.paithani.entity.ProductQuestion;
import java.util.List;

public interface ProductQuestionService {
    ProductQuestion askQuestion(Long productId, String customerName, String customerEmail, String question);
    ProductQuestion answerQuestion(Long questionId, String answer);
    List<ProductQuestion> getQuestionsByProduct(Long productId);
    List<ProductQuestion> getAnsweredQuestionsByProduct(Long productId);
    List<ProductQuestion> getAllQuestions();
}
