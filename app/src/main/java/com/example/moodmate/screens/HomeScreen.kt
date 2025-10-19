package com.example.moodmate.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moodmate.R
import com.example.moodmate.viewModel.JournalViewModel
import com.example.moodmate.viewModel.MoodViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.*
import android.util.Log

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    journalViewModel: JournalViewModel = viewModel(),
    moodViewModel: MoodViewModel = viewModel()
) {
    val currentDate = remember {
        val formatter = SimpleDateFormat("EEEE, MMMM d • yyyy", Locale.getDefault())
        formatter.format(Date())
    }
    var name by remember {
        mutableStateOf("")
    }

    val latestJournal by journalViewModel.latestJournal.collectAsState()
    val isJournalLoading by journalViewModel.isLoading.collectAsState()

    val latestMoodEntry by moodViewModel.latestMoodEntry.collectAsState()
    val isMoodLoading by moodViewModel.isLoading.collectAsState()

    LaunchedEffect(key1 = navController.currentDestination?.route) {
        Firebase.firestore.collection("users")
            .document(FirebaseAuth.getInstance().currentUser!!.uid)
            .get().addOnCompleteListener() {
                name= it.result.get("name").toString().split(" ")[0]
            }
        // Refresh mood data when screen loads or when navigating back
        Log.d("HomeScreen", "Screen loaded/navigated to, refreshing mood data")
        moodViewModel.refreshMoodData()
    }

    // Log mood data changes
    LaunchedEffect(latestMoodEntry) {
        Log.d("HomeScreen", "Mood data changed: $latestMoodEntry")
    }

    fun getMoodDisplayInfo(): Triple<String, String, Color> {
        Log.d(
            "HomeScreen",
            "getMoodDisplayInfo called, latestMoodEntry: $latestMoodEntry"
        )
        return when (latestMoodEntry?.moodLevel) {
            "Excellent" -> Triple("😄", "Excellent", Color(0xFF4CAF50))
            "Good" -> Triple("😊", "Good", Color(0xFF8BC34A))
            "Okay" -> Triple("😐", "Okay", Color(0xFFFF9800))
            "Low" -> Triple("😔", "Low", Color(0xFFFF5722))
            "Very Low" -> Triple("😰", "Very Low", Color(0xFFF44336))
            else -> {
                Log.d(
                    "HomeScreen",
                    "Mood level not recognized or null: ${latestMoodEntry?.moodLevel}"
                )
                Triple("😊", "Unknown", Color(0xFF9E9E9E))
            }
        }
    }

    fun getPersonalizedMessage(): String {
        return when (latestMoodEntry?.moodLevel) {
            "Excellent" -> "You're radiating positive energy today! Keep up the amazing work! 🌟"
            "Good" -> "You're having a great day! Your positive mood is contagious! ✨"
            "Okay" -> "You're doing alright today. Remember, every day has its ups and downs! 🌈"
            "Low" -> "It's okay to have tough days. Be gentle with yourself and take it one step at a time. 💙"
            "Very Low" -> "You're going through a difficult time. Remember, you're stronger than you know. Consider reaching out to someone. 🤗"
            else -> "How are you feeling today? Take a moment to check in with yourself! 💫"
        }
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            Color(0xFF3F51B5),
                            RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "M",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE0E0E0))
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        tint = Color.Gray
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Welcome,",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black
                )
                Text(
                    text = name,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
            }

            Text(
                text = currentDate,
                fontSize = 16.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Normal
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = if (latestMoodEntry != null) "Your current mood" else "How are you feeling today?",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (latestMoodEntry != null) {
                            getMoodDisplayInfo().third.copy(alpha = 0.1f)
                        } else {
                            Color(0xFFE3F2FD)
                        }
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    if (isMoodLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color(0xFF1976D2)
                            )
                        }
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            val (emoji, moodText, moodColor) = getMoodDisplayInfo()

                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .background(
                                        if (latestMoodEntry != null) moodColor else Color(0xFF1976D2),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = emoji,
                                    fontSize = 28.sp
                                )
                            }

                            Column {
                                Text(
                                    text = if (latestMoodEntry != null) moodText else "Unknown",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.Black
                                )
                                Text(
                                    text = getPersonalizedMessage(),
                                    fontSize = 14.sp,
                                    color = Color.Gray,
                                    fontWeight = FontWeight.Normal,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )

                                if (latestMoodEntry != null) {
                                    Text(
                                        text = "Last updated: ${
                                            SimpleDateFormat(
                                                "MMM dd, hh:mm a",
                                                Locale.getDefault()
                                            ).format(Date(latestMoodEntry!!.timestamp))
                                        }",
                                        fontSize = 12.sp,
                                        color = Color.Gray,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }
                        }

                        if (latestMoodEntry == null ||
                            (System.currentTimeMillis() - latestMoodEntry!!.timestamp) > 24 * 60 * 60 * 1000
                        ) { // 24 hours
                            Button(
                                onClick = {
                                    navController.navigate("MoodAssessment")
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF3F51B5),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp)
                                    .padding(bottom = 20.dp)
                            ) {
                                Text(
                                    text = if (latestMoodEntry == null) "Take Mood Assessment" else "Update Mood Assessment",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "My journal",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        Color(0xFFE0E0E0)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    if (isJournalLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else if (latestJournal != null) {
                        // Display latest journal
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    if (latestJournal!!.title.isNotBlank()) {
                                        Text(
                                            text = latestJournal!!.title,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color.Black,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    Text(
                                        text = latestJournal!!.note,
                                        fontSize = 14.sp,
                                        color = Color.Gray,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    Text(
                                        text = SimpleDateFormat(
                                            "MMM dd, yyyy • hh:mm a",
                                            Locale.getDefault()
                                        )
                                            .format(Date(latestJournal!!.timestamp)),
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }

                                // Mood indicator
                                if (latestJournal!!.mood.isNotBlank()) {
                                    Text(
                                        text = latestJournal!!.mood,
                                        fontSize = 24.sp,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    navController.navigate("MyJournals")
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF3F51B5),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "View All Journals",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    } else {
                        // No journals yet
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "You don't have a journal created yet",
                                fontSize = 16.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Normal
                            )

                            Button(
                                onClick = {
                                    navController.navigate("MyJournals")
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(24.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    Color(0xFFE0E0E0)
                                ),
                                elevation = ButtonDefaults.buttonElevation(
                                    defaultElevation = 0.dp
                                ),
                                modifier = Modifier.height(48.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = Color.Black
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Create journal",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    var selectedItem by remember { mutableStateOf(0) }
    val items = listOf(
        NavigationItem("Home", Icons.Default.Home),
        NavigationItem("Mood", Icons.Default.Face),
        NavigationItem("Journal", Icons.Default.Create)
    )

    NavigationBar(
        containerColor = Color.White,
        contentColor = Color.Black,
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedItem == index,
                onClick = {
                    selectedItem = index
                    when (item.label) {
                        "Home" -> navController.navigate("Home")
                        "Mood" -> navController.navigate("MoodAssessment")
                        "Journal" -> navController.navigate("GlobalJournal")
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                },
            )
        }
    }
}

data class NavigationItem(
    val label: String,
    val icon: ImageVector
)