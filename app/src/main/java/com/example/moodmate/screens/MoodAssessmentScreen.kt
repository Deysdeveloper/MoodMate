package com.example.moodmate.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.moodmate.model.MoodOption
import com.example.moodmate.model.MoodQuestion




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodAssessmentScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var answers by remember { mutableStateOf(mapOf<Int, Int>()) }
    var showResult by remember { mutableStateOf(false) }

    val questions = listOf(
        MoodQuestion(
            "How are you feeling right now?",
            listOf(
                MoodOption("Very Happy", 5, "😄"),
                MoodOption("Happy", 4, "😊"),
                MoodOption("Neutral", 3, "😐"),
                MoodOption("Sad", 2, "😢"),
                MoodOption("Very Sad", 1, "😭")
            )
        ),
        MoodQuestion(
            "How would you describe your energy level?",
            listOf(
                MoodOption("Very Energetic", 5, "⚡"),
                MoodOption("Energetic", 4, "🔋"),
                MoodOption("Moderate", 3, "🔄"),
                MoodOption("Low", 2, "🪫"),
                MoodOption("Exhausted", 1, "😴")
            )
        ),
        MoodQuestion(
            "How has your day been so far?",
            listOf(
                MoodOption("Amazing", 5, "🌟"),
                MoodOption("Good", 4, "👍"),
                MoodOption("Okay", 3, "👌"),
                MoodOption("Not great", 2, "👎"),
                MoodOption("Terrible", 1, "💔")
            )
        ),
        MoodQuestion(
            "How do you feel about yourself today?",
            listOf(
                MoodOption("Very confident", 5, "💪"),
                MoodOption("Confident", 4, "😌"),
                MoodOption("Neutral", 3, "🤷"),
                MoodOption("Insecure", 2, "😟"),
                MoodOption("Very insecure", 1, "😰")
            )
        ),
        MoodQuestion(
            "How stressed do you feel?",
            listOf(
                MoodOption("Not stressed", 5, "🧘"),
                MoodOption("Slightly stressed", 4, "😊"),
                MoodOption("Moderately stressed", 3, "😐"),
                MoodOption("Very stressed", 2, "😰"),
                MoodOption("Overwhelmed", 1, "🤯")
            )
        )
    )

    fun calculateMoodResult(): Triple<String, String, Color> {
        val totalScore = answers.values.sum()
        val averageScore = if (answers.isNotEmpty()) totalScore.toFloat() / answers.size else 0f
        return when {
            averageScore >= 4.5 -> Triple(
                "Excellent",
                "You're feeling fantastic! 🌟",
                Color(0xFF4CAF50)
            )
            averageScore >= 3.5 -> Triple("Good", "You're in a positive mood! 😊", Color(0xFF8BC34A))
            averageScore >= 2.5 -> Triple("Okay", "You're feeling alright 😐", Color(0xFFFF9800))
            averageScore >= 1.5 -> Triple("Low", "You might be feeling down 😔", Color(0xFFFF5722))
            else -> Triple("Very Low", "Consider talking to someone 💙", Color(0xFFF44336))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (showResult) "Your Mood Result" else "Mood Assessment",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        Log.d("Navigation", "Back button clicked")
                        navController.navigateUp()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black
                )
            )
        }
    ) { paddingValues ->
        if (showResult) {
            // Show result screen
            val (moodLevel, description, color) = calculateMoodResult()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = color.copy(alpha = 0.1f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Your Mood Level",
                            fontSize = 18.sp,
                            color = Color.Gray
                        )

                        Text(
                            text = moodLevel,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = color
                        )

                        Text(
                            text = description,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            color = Color.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        Log.d("Navigation", "Back to home button clicked")
                        navController.navigate("Home")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3F51B5)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Back to Home",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = {
                        Log.d("Quiz", "Take quiz again button clicked")
                        currentQuestionIndex = 0
                        answers = mapOf()
                        showResult = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF3F51B5)
                    )
                ) {
                    Text(
                        text = "Take Quiz Again",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        } else {
            // Show quiz questions
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp)
            ) {
                // Progress indicator
                LinearProgressIndicator(
                    progress = { (currentQuestionIndex + 1).toFloat() / questions.size },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = Color(0xFF3F51B5),
                    trackColor = Color(0xFFE0E0E0)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Question ${currentQuestionIndex + 1} of ${questions.size}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = questions[currentQuestionIndex].question,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(32.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(questions[currentQuestionIndex].options) { index, option ->
                        val isSelected = answers[currentQuestionIndex] == option.value

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    Log.d("Option", "Option ${option.text} clicked")
                                    answers = answers.toMutableMap().apply {
                                        put(currentQuestionIndex, option.value)
                                    }
                                },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) Color(0xFF3F51B5).copy(alpha = 0.1f) else Color.White
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) Color(0xFF3F51B5) else Color(0xFFE0E0E0)
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = if (isSelected) 4.dp else 2.dp
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(
                                    text = option.emoji,
                                    fontSize = 32.sp
                                )

                                Text(
                                    text = option.text,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isSelected) Color(0xFF3F51B5) else Color.Black,
                                    modifier = Modifier.weight(1f)
                                )

                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = Color(0xFF3F51B5)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        if (currentQuestionIndex < questions.size - 1) {
                            Log.d("Navigation", "Next question button clicked")
                            currentQuestionIndex++
                        } else {
                            Log.d("Quiz", "See results button clicked")
                            showResult = true
                        }
                    },
                    enabled = answers.containsKey(currentQuestionIndex),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3F51B5)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = if (currentQuestionIndex < questions.size - 1) "Next Question" else "See Results",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}