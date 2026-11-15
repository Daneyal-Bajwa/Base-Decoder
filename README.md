# All-purpose Base Decoding Tool

A lightweight java tool built to match input base-encoded string of unknown base origin against various bases and decode it. 

## Core Features
* **One input, multiple outputs** Validates input string against various popular known bases, and attempts to convert it with each base.
* **Easy addition of new bases:** Adding a new base is as simple as adding its alphabet in its logical order along with its base number as a tuple in the Map at the beginning of the script.
* **Universal function for all bases** One universal function avoids repetition of logic whilst making error-handling easy from a single interface.
* **Input validation using regular expression** Before attempting to decode a base-encoded string against a base, it is filtered through regular expression to ensure it fits the alphabet of the corresponding base.
* **Dynamic Network Detection:** Configured with K8s `hostNetwork` to dynamically detect and scan the local physical subnet.

## Prerequisites
* Java 9 or older

## Technical Skills Demonstrated
* Base encoding and decoding mathematical skills
* Knowledge of various popular bases around
* Reusability of functions ensuring logic is sound and repetition is avoided  


