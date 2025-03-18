package com.alexey.quizappmvvm.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexey.quizappmvvm.data.model.Question
import com.alexey.quizappmvvm.data.repository.QuestionRepository
import com.alexey.quizappmvvm.utils.QuizTypes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


open class QuizViewModel(private var repository: QuestionRepository?) : ViewModel() {

    internal val _questions = MutableStateFlow<List<Question>>(emptyList())
    val questions: StateFlow<List<Question>> = _questions

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex

    private val _correctCount = MutableStateFlow(0)
    val correctCount: StateFlow<Int> = _correctCount

    private val _totalCount = MutableStateFlow(0)
    val totalCount: StateFlow<Int> = _totalCount

    internal val _userAnswer = MutableStateFlow<Int?>(null)
    val userAnswer: StateFlow<Int?> = _userAnswer

    fun loadQuestions(quizType: String) {
        viewModelScope.launch {
            val randomQuestions = when (quizType) {
                QuizTypes.EASY -> repository?.getEasyQuestions()
                QuizTypes.NORMAL -> repository?.getNormalQuestions()
                QuizTypes.HARD -> repository?.getHardQuestions()
                QuizTypes.INSANE -> repository?.getInsaneQuestions()
                else -> emptyList()
            }
            _questions.value = randomQuestions!!
            _totalCount.value = _questions.value.size
        }
    }

    fun selectAnswer(selectedOption: Int) {
        if (_userAnswer.value != null) return

        val currentQuestion = _questions.value.getOrNull(_currentQuestionIndex.value)
        _userAnswer.value = selectedOption

        if (currentQuestion != null && selectedOption == currentQuestion.correctOption) {
            _correctCount.value++
        }
    }

    fun nextQuestion() {
        _userAnswer.value = null
        if (_currentQuestionIndex.value < _questions.value.lastIndex) {
            _currentQuestionIndex.value++
        }
    }

    fun resetQuiz() {
        _questions.value = emptyList()
        _currentQuestionIndex.value = 0
        _correctCount.value = 0
        _userAnswer.value = null
    }
}
