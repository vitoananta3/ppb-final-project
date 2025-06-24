package com.example.gasnugas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.gasnugas.ui.HomeScreen
import com.example.gasnugas.ui.ProfileScreen
import com.example.gasnugas.ui.auth.AuthViewModel
import com.example.gasnugas.ui.auth.LoginScreen
import com.example.gasnugas.ui.auth.RegisterScreen
import com.example.gasnugas.ui.theme.GasnugasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GasnugasTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GasnugasApp()
                }
            }
        }
    }
}

data class BottomNavItem(
    val title: String,
    val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val route: String
)

@Composable
fun GasnugasApp() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    
    // Observe current user session
    val currentUser by authViewModel.currentUser.collectAsState(initial = null)
    
    // Determine start destination based on authentication state
    val startDestination = if (currentUser != null) "main" else "login"
    
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("login") {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate("register") {
                        launchSingleTop = true
                    }
                },
                onLoginSuccess = {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                        launchSingleTop = true
                    }
                },
                authViewModel = authViewModel
            )
        }
        
        composable("register") {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onRegisterSuccess = {
                    navController.navigate("main") {
                        popUpTo("register") { inclusive = true }
                        launchSingleTop = true
                    }
                },
                authViewModel = authViewModel
            )
        }
        
        composable("main") {
            MainScreenWithBottomNav(
                authViewModel = authViewModel,
                onLogout = {
                    authViewModel.logout()
                    navController.navigate("login") {
                        popUpTo("main") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

@Composable
fun MainScreenWithBottomNav(
    authViewModel: AuthViewModel,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    
    val bottomNavItems = listOf(
        BottomNavItem(
            title = "Home",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            route = "home"
        ),
        BottomNavItem(
            title = "Profile",
            selectedIcon = Icons.Filled.Person,
            unselectedIcon = Icons.Outlined.Person,
            route = "profile"
        )
    )
    
    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                
                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = if (currentDestination?.hierarchy?.any { it.route == item.route } == true) {
                                    item.selectedIcon
                                } else {
                                    item.unselectedIcon
                                },
                                contentDescription = item.title
                            )
                        },
                        label = { Text(item.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("home") {
                HomeScreen(authViewModel = authViewModel)
            }
            
            composable("profile") {
                ProfileScreen(
                    authViewModel = authViewModel,
                    onLogout = onLogout
                )
            }
        }
    }
}