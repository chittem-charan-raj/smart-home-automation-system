# Smart Home Automation System

An object-oriented home automation simulator with a real-time Java Swing control panel and background task scheduling.

## Overview
This project models a home automation system as a set of independently controllable devices, each with its own state and behavior, unified under a common `Device` abstraction — then exposes them through a live Swing GUI with scheduling support.

## Design

**Abstraction:** An abstract `Device` base class defines shared behavior (`toggle()`, `isOn()`, `getStatus()`), with each concrete device overriding `getStatus()` to report its own state:
- `Light` — on/off + brightness (0–100%)
- `Fan` — on/off + speed (0–5)
- `AC` / `Heater` — on/off + temperature (with realistic range validation: AC 16–30°C, Heater 20–35°C)
- `SecuritySystem` — enabled/disabled
- `Sprinkler` — on/off

**Concurrency:** A `Scheduler` class runs on a **daemon background thread**, checking every minute whether any scheduled task should fire. Scheduling a device "ON" for a duration automatically queues a matching "OFF" task, so a fan set to run for 30 minutes turns itself off without further input — no manual cleanup required.

**UI:** Java Swing (`JFrame`, `GridLayout` button panel, `JTextArea` live status feed) with input validation via `JOptionPane` dialogs — invalid brightness/speed/temperature values are rejected with a message rather than silently accepted.

## What's Included
- `HomeAutomationSystemGUI.java` — complete single-file application: main GUI class, 6 device classes, and the multithreaded scheduler

## Tech Stack
- Java
- Swing (GUI)
- `java.util.concurrent`-style daemon threading for the scheduler
- `java.time.LocalTime` for schedule handling

## How to Run
```bash
javac HomeAutomationSystemGUI.java
java HomeAutomationSystemGUI
```

## Key Insight
The scheduler runs independently of the UI thread (as a daemon thread), so scheduled device changes update the live status feed in real time without blocking user interaction — a small but deliberate concurrency design choice rather than a single-threaded polling loop.
