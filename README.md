# pi-sprinkler

A sprinkler system application that runs on a [Raspberry Pi](http://www.raspberrypi.org/) that talks to an [OpenSprinkler Pi](http://rayshobby.net/?page_id=5816) board.

## Installation

Download the source from https://github.com/tedfoye/pi-sprinkler

## Hardware

This application requires
* A Rasberry Pi
  * I am using a Model B configured to run Raspian
* An OpenSprinkler Pi board
  * You can get one from [Ray's DIY Electronics Hobby Projects](http://rayshobby.net/?page_id=5816)
  * 
## Usage

This program uses the [Google Calendar API](https://developers.google.com/google-apps/calendar/). Visit the [getting started](https://developers.google.com/google-apps/calendar/get_started) page to get remote access to your Google calendar.

Once you have your calendar configured for remote access you will need to update the `client_secrets.json` file. Here is what the default file contains:
```json
{
  "installed": {
    "client_id": "837647042410-75ifg...usercontent.com",
    "client_secret":"asdlkfjaskd",
    "redirect_uris": ["http://localhost", "urn:ietf:wg:oauth:2.0:oob"],
    "auth_uri": "https://accounts.google.com/o/oauth2/auth",
    "token_uri": "https://accounts.google.com/o/oauth2/token"
  }
}
```

Update this file your calendar configuration.

You will also need to update `events.clj`. Replace `id@group.calendar.google.com` with your calendar ID.

## License

Copyright © 2014 Ted Foye

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
