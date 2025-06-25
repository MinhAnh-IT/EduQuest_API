package com.vn.EduQuest.services;

import com.vn.EduQuest.entities.Answer;
import com.vn.EduQuest.entities.Question;
import com.vn.EduQuest.entities.User;
import com.vn.EduQuest.enums.StatusCode;
import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.mapper.QuestionMapper;
import com.vn.EduQuest.payload.response.answer.AnswerResponse;
import com.vn.EduQuest.payload.response.question.QuestionCreateResponse;
import com.vn.EduQuest.payload.response.question.QuestionResponse;
import com.vn.EduQuest.repositories.AnswerRepository;
import com.vn.EduQuest.repositories.QuestionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuestionServiceImpl implements QuestionService {
    QuestionRepository questionRepository;
    AnswerRepository answerRepository;
    QuestionMapper questionMapper;
    UserService userService;

    @Override
    public long getCorrectAnswerId(long questionId) throws CustomException {
        var question = getQuestionById(questionId);
        for (var answer : question.getAnswers()) {
            if (answer.getIsCorrect()) {
                return answer.getId();
            }
        }
        throw new CustomException(StatusCode.QUESTION_NOT_ANSWER_CORRECT, questionId);
    }

    @Override
    public Question getQuestionById(long questionId) throws CustomException {
        return questionRepository.findById(questionId)
                .orElseThrow(() -> new CustomException(StatusCode.NOT_FOUND, "question", questionId));
    }

    @Override
    public QuestionResponse getQuestionResponseById(long questionId) throws CustomException {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new CustomException(StatusCode.NOT_FOUND, questionId));

        List<Answer> answers = answerRepository.findByQuestionId(questionId);

        return QuestionResponse.builder()
                .id(Math.toIntExact(question.getId()))
                .content(question.getContent())
                .difficulty(question.getDifficulty().name())
                .answers(AnswerResponse.fromAnswers(answers))
                .build();
    }

    @Override
    public QuestionCreateResponse createQuestion(Question question, Long instructorId) throws CustomException {
        User user = userService.getUserById(instructorId);
        if (user == null) {
            throw new CustomException(StatusCode.USER_NOT_FOUND);
        }
        if (user.isStudent()) {
            throw new CustomException(StatusCode.FORBIDDEN);
        }

        if (question.getAnswers() == null || question.getAnswers().size() < 2) {
            throw new CustomException(StatusCode.QUESTION_MUST_HAVE_ANSWERS);
        }
        if (question.getAnswers().stream().noneMatch(Answer::getIsCorrect)) {
            throw new CustomException(StatusCode.QUESTION_NOT_INCLUDE_ANSWER_CORRECT);
        }

        question.setCreatedBy(user);

        for (Answer answer : question.getAnswers()) {
            answer.setQuestion(question);
            answer.setCreatedAt(LocalDateTime.now());
        }

        Question savedQuestion = questionRepository.save(question);
        return questionMapper.toQuestionCreateResponse(savedQuestion);
    }

    @Override
    public QuestionCreateResponse updateQuestion(Long questionId, Question question, Long instructorId) throws CustomException {
        User user = userService.getUserById(instructorId);
        if (user == null) {
            throw new CustomException(StatusCode.USER_NOT_FOUND);
        }
        if (user.isStudent()) {
            throw new CustomException(StatusCode.FORBIDDEN);
        }
        if (question.getAnswers() == null || question.getAnswers().size() < 2) {
            throw new CustomException(StatusCode.QUESTION_MUST_HAVE_ANSWERS);
        }
        if (question.getAnswers().stream().noneMatch(Answer::getIsCorrect)) {
            throw new CustomException(StatusCode.QUESTION_NOT_INCLUDE_ANSWER_CORRECT);
        }

        Question existingQuestion = getQuestionById(questionId);
        existingQuestion.setContent(question.getContent());
        existingQuestion.setDifficulty(question.getDifficulty());
        existingQuestion.setCreatedBy(user);

        existingQuestion.getAnswers().removeIf(
                oldAnswer -> question.getAnswers().stream()
                        .noneMatch(newAnswer -> Objects.equals(newAnswer.getId(), oldAnswer.getId()))
        );

        for (Answer newAnswer : question.getAnswers()) {
            newAnswer.setQuestion(existingQuestion);
            if (newAnswer.getId() == null) {
                newAnswer.setCreatedAt(LocalDateTime.now());
                newAnswer.setUpdatedAt(LocalDateTime.now());
                existingQuestion.getAnswers().add(newAnswer);
            } else {
                existingQuestion.getAnswers().stream()
                        .filter(a -> Objects.equals(a.getId(), newAnswer.getId()))
                        .findFirst()
                        .ifPresent(a -> {
                            a.setContent(newAnswer.getContent());
                            a.setIsCorrect(newAnswer.getIsCorrect());
                            a.setUpdatedAt(LocalDateTime.now());
                        });
            }
        }
        Question updatedQuestion = questionRepository.save(existingQuestion);
        return questionMapper.toQuestionCreateResponse(updatedQuestion);
    }
    @Override
    public List<QuestionCreateResponse> getQuestionsCreatedByInstructor(Long instructorId) throws CustomException {
        User user = userService.getUserById(instructorId);
        if (user == null) {
            throw new CustomException(StatusCode.USER_NOT_FOUND);
        }
        if (user.isStudent()) {
            throw new CustomException(StatusCode.FORBIDDEN);
        }

        List<Question> questions = questionRepository.findByCreatedById(instructorId);
        return questions.stream()
                .map(questionMapper::toQuestionCreateResponse)
                .collect(Collectors.toList());
    }
}
