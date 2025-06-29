package com.example.moodmate.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.moodmate.model.JournalEntry
import com.example.moodmate.screens.AddJournalScreen
import com.example.moodmate.screens.AuthenticationScreen
import com.example.moodmate.screens.HomeScreen
import com.example.moodmate.screens.Journals.GlobalJournalsScreen
import com.example.moodmate.screens.Journals.MyJournalsScreen
import com.example.moodmate.screens.MoodAssessmentScreen
import com.example.moodmate.screens.SignInScreen
import com.example.moodmate.screens.SignUpScreen

@Composable
fun Navigation(modifier: Modifier)
{
    val navController = rememberNavController()
    NavHost(navController = navController,
        startDestination ="Auth"
    )
    {
        composable("Auth")
        {
            AuthenticationScreen(modifier = Modifier,navController)
        }
        composable("SignUp") {
            SignUpScreen(modifier=Modifier,navController)

        }
        composable("SignIn") {
            SignInScreen(modifier= Modifier,navController)
        }
        composable("Home") {
            HomeScreen(modifier= Modifier,navController)
        }
        composable("MoodAssessment") {
            MoodAssessmentScreen(modifier = Modifier, navController)
        }
        composable("AddJournal") {
            AddJournalScreen(modifier= Modifier,navController)
        }
        composable("GlobalJournal") {
            GlobalJournalsScreen(modifier = Modifier, navController)
        }
        composable("MyJournals") {
            MyJournalsScreen(modifier= Modifier,navController)
        }
        }


    }