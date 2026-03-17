# SnakeGame.java
Java Swing Snake Game with dynamic walls, scoring system, and collision handling
🐍 Snake Game (Java Swing)

A feature-rich Snake Game developed using Java Swing, implementing object-oriented design principles and dynamic gameplay mechanics such as obstacle generation, collision handling, and real-time updates.

🎮 Features

Smooth grid-based snake movement

Object-Oriented Design using inheritance (GameEntity)

Dynamic food generation with random colors

Real-time scoring system

Pause and resume functionality

Restart option after game over

🧱 Advanced Gameplay

Dynamic golden wall generation after score threshold

Random wall size and orientation

Collision system:

Boundary collision (with limited retries)

Golden wall collision

Self collision

Warning system before final game over

Snake reset after minor collisions

🧠 Technical Highlights

Built using Java Swing (GUI framework)

Game loop implemented using javax.swing.Timer

Separate timers for:

Game updates

Wall spawning

Clean OOP structure:

GameEntity (abstract base class)

SnakeSegment

Food

Wall

Efficient position checking:

isPositionOccupied()

isSnakeAt()

isWallAt()

🎯 Controls
Key	Action
↑ ↓ ← →	Move Snake
SPACE	Pause / Resume / Restart
ESC	Exit Game
⚙️ How to Run

Clone the repository:

git clone https://github.com/YOUR_USERNAME/SnakeGame-Java.git

Open in IntelliJ IDEA (or any Java IDE)

Run:

SnakeGame.java

📸 Screenshot

<img width="477" height="342" alt="Screenshot 2026-03-17 at 7 47 09 PM" src="https://github.com/user-attachments/assets/23754c7e-d200-427d-8eaa-fef09587d05a" />
<img width="1166" height="742" alt="Screenshot 2026-03-17 at 7 40 12 PM" src="https://github.com/user-attachments/assets/bd4d9b4b-fa34-4e99-a0e3-1ad9e2fe18c6" />
<img width="1166" height="742" alt="Screenshot 2026-03-17 at 7 39 06 PM" src="https://github.com/user-attachments/assets/6f4da8b6-7c7b-48a0-9e34-09fb900ae1ff" />


🚧 Future Improvements

Add sound effects

Difficulty levels (Easy / Medium / Hard)

High score saving system

Main menu screen

Better UI styling (JavaFX version)

👩‍💻 Author

Umme Mehajabin Anisha

CSE Student
