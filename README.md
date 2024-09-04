# ViewTube - Android Project
## Overview
ViewTube is a video-sharing platform that allows users to upload, watch, and interact with videos. The Android application mirrors all core functionalities of the web version, including video playback, commenting, like/dislike, video uploading, and user management, all optimized for mobile use.

## Features
- **Video Playback:** Enjoy videos with a responsive and mobile-friendly player.
- **Video Details:** Manage your video titles and descriptions directly from your device.
- **Video Sharing:** Share videos effortlessly with your friends.
- **Comments Section:** Interact with the community through comments.
- **Related Videos:** Explore related videos based on your viewing.
- **User Management:** Create and sign into your created user, upload videos and manage your likes/dislikes, videos uploaded and your comments.
- **Dark Mode:** Customize your viewing preference with Dark Mode.

## Technologies Used
- **Frontend:** Android SDK
- **Styling:** XML with Material Components

## Prerequisites
Before running the application, make sure you have the following:
- Android Studio (version 4.0 or later)
- An Android device or emulator running API level 21 (Lollipop) or higher

## Installation
### Clone the Repository:
git clone https://github.com/ZivElbaz/android-video-sharing-platform
### Open the Project:
Open Android Studio.
Import the project you just cloned.
### Update the Base URL:
- Go to the Config in the manager folder and update the base_url configuration to match the IP address where the server is running.
- If running on the emulator, use 10.0.2.2 as the base URL
- If running on a real device use your machine's IP as the base URL
### Run the Project:
Build the project and run it on your Android device or emulator.

## Working Process
The development process for the Android app paralleled the collaborative efforts of our web platform, with tasks divided among team members tailored to mobile development. Here's how we approached it:
- **Task Assignment:** Distinct modules like video playback, user management, and community interactions were assigned to specific team members.
- **Design Implementation:** Mobile-specific designs were implemented ensuring an intuitive and user-friendly interface.
- **Building Logical Infrastructure:** Developed mobile-centric logic using Android architecture components for robust state management.
- **Coordination and Integration:** Regular synchronization ensured seamless integration of components into the mobile app.
- **Testing and Refinement:** Comprehensive testing on various devices ensured a smooth and bug-free experience. Adjustments were made to enhance performance and usability.

## Branching Information
- Part 1 (Main Android Features): All the core functionalities of the Android application are located in the main branch.
- Part 3 (Server Implementation): For the server-side implementation and integration, check the server_implement branch.

## Working Team:
- **Ofek Baribi**
- **Ziv Elbaz**
- **Yuval Maaravi**

By employing a focused approach on mobile-specific functionalities and design, the Android app aims to provide an engaging and seamless video sharing experience that complements our web platform.
