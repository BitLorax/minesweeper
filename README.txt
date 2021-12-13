=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=
CIS 120 Game Project README
PennKey: wjhliang
=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=

===================
=: Core Concepts :=
===================

- List the four core concepts, the features they implement, and why each feature
  is an appropriate use of the concept. Incorporate the feedback you got after
  submitting your proposal.

  1. 2D Arrays: I used a 2D integer array to implement the game board, which is the 16x16 grid the
  player sees as they play the game. Each integer in the array represents a certain tile (-1 for
  hidden, -2 for flag, -3 for bomb, and non-negative integers representing a revealed tile with
  the number of bombs around it). I also used a 2D boolean array to keep track of the bomb
  locations, with true being a location with a bomb and false otherwise.

  2. File IO: I used file IO to keep a permanent game state across game sessions so the player
  doesn't lose progress when they close and reopen the game. Whenever a move is made, I overwrite
  the save file (minesweeper_save.txt) with the current game state (in progress, win, loss),
  board width and height, number of mines, the two 2D arrays detailed above, and a list of moves
  the player made in the current game (detailed below). When the game launches, it checks if the
  save file exists, and loads the data if it's found and compatible.

  3. Recursion: I used recursion to reveal all adjacent non-bomb tiles when the player reveals an
  empty tile (tile with 0 bombs around it). The recursive dfs method visits its adjacent tiles in
  the 4 cardinal directions and checks if it's also empty; if it is, then it visits its neighbors
  as well, and if it's not, then it reveals the tile if it's not a bomb and then stops.

  4. Collections: I used collections to implement a replay feature that plays back the player's
  moves starting from the start of the game. I kept track of the player's moves in the current
  game with an ArrayList of integer arrays, with each entry containing the move's x position, y
  position, and whether the move is a left click (reveal tile) or right click (place/remove flag).

=========================
=: Your Implementation :=
=========================

- Provide an overview of each of the classes in your code, and what their
  function is in the overall game.

My game consists of 3 classes: GameBoard, Minesweeper, and RunMinesweeper.

GameBoard is primarily in charge of drawing the game itself for the user to see. It loads the
sprites, paints the tiles of the board, and updates the icons for the buttons. Its other function
is to load and reset the game by both calling functions in the Minesweeper class and updating the
graphics.

Minesweeper contains all the processing that makes the game work. This includes playing turns
(calculating new board state when user reveals a tile), updating game state, resetting, saving, and
loading game states, and replaying the game.

RunMinesweeper Sets up the JFrame, including the status/reset button, replay button, and help/
instructions button.

- Were there any significant stumbling blocks while you were implementing your
  game (related to your design, or otherwise)?

The biggest obstacle I faced when implementing my game was figuring out how to implement the
replay animation. At first, I thought that using Thread.sleep() and simply looping over the list of
moves would work, but it freezes the GUI update as well. Thus, I learned about the timer object
and how it can call functions every x milliseconds while allowing the GUI to update. Having the
timer's function be constant required a restructuring of my replay code since I had to store the
replay step and step forward functions in Minesweeper; doing so allows the timer to simply call
ms.replayStepForward() every x milliseconds and let the minesweeper class handle the
actual updating.

- Evaluate your design. Is there a good separation of functionality? How well is
  private state encapsulated? What would you refactor, if given the chance?

I think my design is pretty simple, but it clearly separates the board's visual representation and
the actual game logic behind it. Besides the constants, all fields are encapsulated in
Minesweeper and GameBoard, and anything that modifies the fields externally uses methods defined
in the class. If given the chance, I would probably make a separate class to better store moves
instead of using integer arrays, but I think it's still clear and simple as it is.

========================
=: External Resources :=
========================

- Cite any external resources (images, tutorials, etc.) that you may have used 
  while implementing your game.

Sprite image source: https://www.spriters-resource.com/pc_computer/minesweeper/sheet/19849/
I modified the downloaded image a bit using Aseprite to make custom buttons for replay and
instructions.