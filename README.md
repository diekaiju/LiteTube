<p align="center"><img src="screenshots\Screenshot 2026-04-20 123334.png" width="150"></p> 
<h2 align="center"><b>LiteTube</b></h2>
<h4 align="center">A stable, lightweight, and privacy-focused streaming front-end for Android.</h4>

<p align="center">
<a href="https://github.com/diekaiju/LiteTube/releases" alt="GitHub LiteTube releases"><img src="https://img.shields.io/github/release/diekaiju/LiteTube.svg" ></a>
<a href="https://www.gnu.org/licenses/gpl-3.0" alt="License: GPLv3"><img src="https://img.shields.io/badge/License-GPL%20v3-blue.svg"></a>
<a href="https://github.com/diekaiju/LiteTube/actions" alt="Build Status"><img src="https://github.com/diekaiju/LiteTube/actions/workflows/ci.yml/badge.svg?branch=dev&event=push"></a>
</p>

<hr>
<p align="center"><a href="#screenshots">Screenshots</a> &bull; <a href="#supported-services">Supported Services</a> &bull; <a href="#description">Description</a> &bull; <a href="#features">Features</a> &bull; <a href="#installation-and-updates">Installation and updates</a> &bull; <a href="#contribution">Contribution</a> &bull; <a href="#donate">Donate</a> &bull; <a href="#license">License</a></p>
<hr>

*Read this document in other languages: [Deutsch](doc/README.de.md), [English](README.md), [Español](doc/README.es.md), [Français](doc/README.fr.md), [हिन्दी](doc/README.hi.md), [Italiano](doc/README.it.md), [한국어](doc/README.ko.md), [Português Brasil](doc/README.pt_BR.md), [Polski](doc/README.pl.md), [ਪੰਜਾਬੀ ](doc/README.pa.md), [日本語](doc/README.ja.md), [Română](doc/README.ro.md), [Soomaali](doc/README.so.md), [Türkçe](doc/README.tr.md), [正體中文](doc/README.zh_TW.md), [অসমীয়া](doc/README.asm.md), [Српски](doc/README.sr.md), [العربية](README.ar.md)* 

> [!warning]
> <b>THIS APP IS IN BETA, SO YOU MAY ENCOUNTER BUGS. IF YOU DO, OPEN AN ISSUE IN OUR GITHUB REPOSITORY BY FILLING OUT THE ISSUE TEMPLATE.</b>
> 
> <b>PUTTING NEWPIPE, OR ANY FORK OF IT, INTO THE GOOGLE PLAY STORE VIOLATES THEIR TERMS AND CONDITIONS.</b>

## Screenshots

[<img src="screenshots/image%20(1).jpeg" width=160>](screenshots/image%20(1).jpeg)
[<img src="screenshots/image%20(2).jpeg" width=160>](screenshots/image%20(2).jpeg)
[<img src="screenshots/image%20(3).jpeg" width=160>](screenshots/image%20(3).jpeg)
[<img src="screenshots/image%20(4).jpeg" width=160>](screenshots/image%20(4).jpeg)
[<img src="screenshots/image%20(5).jpeg" width=160>](screenshots/image%20(5).jpeg)
[<img src="screenshots/image%20(6).jpeg" width=160>](screenshots/image%20(6).jpeg)
[<img src="screenshots/image%20(7).jpeg" width=160>](screenshots/image%20(7).jpeg)

### Supported Services

LiteTube currently supports these services:

<!-- We link to the service websites separately to avoid people accidentally opening a website they didn't want to. -->
* YouTube ([website](https://www.youtube.com/)) and YouTube Music ([website](https://music.youtube.com/)) ([wiki](https://en.wikipedia.org/wiki/YouTube))
* PeerTube ([website](https://joinpeertube.org/)) and all its instances (open the website to know what that means!) ([wiki](https://en.wikipedia.org/wiki/PeerTube))
* Bandcamp ([website](https://bandcamp.com/)) ([wiki](https://en.wikipedia.org/wiki/Bandcamp))
* SoundCloud ([website](https://soundcloud.com/)) ([wiki](https://en.wikipedia.org/wiki/SoundCloud))
* media.ccc.de ([website](https://media.ccc.de/)) ([wiki](https://en.wikipedia.org/wiki/Chaos_Computer_Club))

As you can see, LiteTube supports multiple video and audio services. Though it started off as a fork of NewPipe, other people have added more services over the years, making LiteTube more and more versatile!

Partially due to circumstance, and partially due to its popularity, YouTube is the best supported out of these services. If you use or are familiar with any of these other services, please help us improve support for them!

If you intend to add a new service, please get in touch with us first! Our [docs](https://teamnewpipe.github.io/documentation/) provide more information on how a new service can be added to the app and to the [NewPipe Extractor](https://github.com/TeamNewPipe/NewPipeExtractor).

## Description

LiteTube works by fetching the required data from the official API (e.g. PeerTube) of the service you're using. If the official API is restricted (e.g. YouTube) for our purposes, or is proprietary, the app parses the website or uses an internal API instead. This means that you don't need an account on any service to use LiteTube.

Also, since they are free and open source software, neither the app nor the Extractor use any proprietary libraries or frameworks, such as Google Play Services. This means you can use LiteTube on devices or custom ROMs that do not have Google apps installed.

### LiteTube Enhancements

LiteTube is a specialized fork of NewPipe with a focus on stability, improved navigation, and integrated web fallbacks.

*   **Integrated YouTube WebView**: Seamlessly switch to the integrated WebView when native extraction fails, ensuring 100% playback availability.
*   **Enhanced URL Normalization**: Robust handling of mobile YouTube links (`m.youtube.com`) and shortened URLs (`youtu.be`) for effortless sharing and opening from other apps.
*   **Navigation Stability**: Resolved critical runtime crashes (including `PlaylistFragment` NullPointerExceptions) and improved overall back-stack reliability.
*   **Code Quality**: Strict adherence to industrial Checkstyle standards for a clean and professional codebase.

### Features

* Watch videos at resolutions up to 4K
* Listen to audio in the background, only loading the audio stream to save data
* Popup mode (floating player, aka Picture-in-Picture)
* Watch live streams
* Show/hide subtitles/closed captions
* Search videos and audios (on YouTube, you can specify the content language as well)
* Enqueue videos (and optionally save them as local playlists)
* Show/hide general information about videos (such as description and tags)
* Show/hide next/related videos
* Show/hide comments
* Search videos, audios, channels, playlists and albums
* Browse videos and audios within a channel
* Subscribe to channels (yes, without logging into any account!)
* Get notifications about new videos from channels you're subscribed to
* Create and edit channel groups (for easier browsing and management)
* Browse video feeds generated from your channel groups
* View and search your watch history
* Search and watch playlists (these are remote playlists, which means they're fetched from the service you're browsing)
* Create and edit local playlists (these are created and saved within the app, and have nothing to do with any service)
* Download videos/audios/subtitles (closed captions)
* Open in Kodi
* Watch/Block age-restricted material
* **Integrated WebView Fallback**: For resilient playback when native extractors fail

<!-- Hidden span to keep old links compatible. You should remove this span if you're translating the README into another language.-->
<span id="updates"></span>

## Installation and updates
## Installation and updates
You can install LiteTube using one of the following methods:
 1. Download the APK from [GitHub Releases](https://github.com/diekaiju/LiteTube/releases) and install it.
 2. Build a debug APK yourself. This is the fastest way to get new features on your device, but is more complicated.

If you're switching from official NewPipe or another fork, we recommend following this procedure:
1. Back up your data via Settings > Backup and Restore > Export Database so you keep your history, subscriptions, and playlists
2. Uninstall your current version
3. Download the LiteTube APK and install it
4. Import the data from step 1 via Settings > Backup and Restore > Import Database

> [!Note]
> When you're importing a database into the official app, always make sure that it is the one you exported _from_ the official app. If you import a database exported from an APK other than the official app, it may break things. Such an action is unsupported, and you should only do so when you're absolutely certain you know what you're doing.

### APK Info

This is the SHA fingerprint of LiteTube's signing key to verify downloaded APKs.
```
CB:84:06:9B:D6:81:16:BA:FA:E5:EE:4E:E5:B0:8A:56:7A:A6:D8:98:40:4E:7C:B1:2F:9E:75:6D:F5:CF:5C:AB
```

## Contribution
Whether you have ideas, translations, design changes, code cleaning, or even major code changes, help is always welcome. The app gets better and better with each contribution, no matter how big or small! If you'd like to get involved, check our [contribution notes](.github/CONTRIBUTING.md).

<a href="https://hosted.weblate.org/engage/newpipe/">
<img src="https://hosted.weblate.org/widgets/newpipe/-/287x66-grey.png" alt="Translation status" />
</a>

## Donate
If you like LiteTube, you're welcome to send a donation. LiteTube is developed by volunteers spending their free time bringing you the best user experience.

## Privacy Policy

The LiteTube project aims to provide a private, anonymous experience for using web-based media services. Therefore, the app does not collect any data without your consent. LiteTube's privacy policy explains in detail what data is sent and stored when you send a crash report.

## License
[![GNU GPLv3 Image](https://www.gnu.org/graphics/gplv3-127x51.png)](https://www.gnu.org/licenses/gpl-3.0.en.html)  

LiteTube is Free Software: You can use, study, share, and improve it at will. Specifically you can redistribute and/or modify it under the terms of the [GNU General Public License](https://www.gnu.org/licenses/gpl.html) as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
