# Weather App

## Overview
This is a weather app that provides users with real-time weather updates for their current location as well as for their favorite locations. Users can also set weather alerts and receive notifications or alarms based on their preferences.

## Features
- **Current Weather**: Displays up-to-date weather information for the user's location, which is automatically updated when the weather changes.
- **Favorite Locations**: Allows users to add favorite locations and view weather updates for those places.
- **Weather Alerts**: Users can set weather alerts for specific times and choose between receiving a notification or an alarm.
- **Offline Support**: The app detects internet connectivity and handles offline scenarios.

## Key Components
1. **Current Weather**: 
    - The app uses the OpenWeatherMap API to fetch and display real-time weather information for the user's current location.
    - The data is constantly overwritten as the weather changes.

2. **Favorites**: 
    - Users can add locations to their favorites, which are saved and can be accessed even when offline.
    - Weather updates for favorite locations are fetched on demand.

3. **Weather Alerts**: 
    - Users can set weather alerts with specific dates and times.
    - The app allows the user to choose between a notification or an alarm when the alert time is reached.

4. **Offline Handling**: 
    - The app checks for internet connectivity and adjusts its behavior accordingly, ensuring a seamless experience.

## Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/kk98989898/Weather_App.git
   cd weather-app
