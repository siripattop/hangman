import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;


public abstract class ClientGame {
	protected final BufferedReader consoleReader;
	protected PrintWriter out;
	protected int tries;
	private boolean fileInitialized = false;

	public ClientGame(String fileName) {
		this.consoleReader = new BufferedReader(new InputStreamReader(System.in));
		try {
			this.out = new PrintWriter(new File(fileName));
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		}
	}

	public void run() {
		System.out.println("Client-Server Hangman game is starting...");
	}

	protected void toPlayOrNotToPlay() {
		String userInput = null;
		while (true) {
			System.out.print("Do you want to play? (y/n) ");
			try {
				userInput = this.consoleReader.readLine();
			} catch (IOException ex) {
				ex.printStackTrace();
				stopGame();
				break;
			}
			userInput = userInput.trim().toLowerCase();
			if (userInput == null) {
				stopGame();
				break;
			} else if (userInput.equals("y")) {
				if (!this.fileInitialized) {
					FileUtils.initFile(this.out);
					this.fileInitialized = !this.fileInitialized;
				}
				writeToServer(Server.GAME_PLAY);
				this.tries = 0;
				if (!play()) {
					break;
				}
			} else if (userInput.equals("n")) {
				stopGame();
				break;
			} else {
				System.out.println("Invalid input.");
			}
		}
	}

	
	protected abstract boolean play();

	protected void draw_man(int remain){
		switch(remain) {
		  case 8:
			System.out.println("  |---------");
            System.out.println("  |");
			System.out.println("  |");
			System.out.println("  |");
			System.out.println("  |");
			System.out.println("  |");
			System.out.println("  |");
			System.out.println("  |");
			System.out.println("__|_____________");
			break;
		  case 7:
		    System.out.println("  |---------");
            System.out.println("  |        |");
			System.out.println("  |");
			System.out.println("  |");
			System.out.println("  |");
			System.out.println("  |");
			System.out.println("  |");
			System.out.println("  |");
			System.out.println("__|_____________");
			break;
		  case 6:
			System.out.println("  |---------");
            System.out.println("  |        |");
			System.out.println("  |        0");
			System.out.println("  |");
			System.out.println("  |");
			System.out.println("  |");
			System.out.println("  |");
			System.out.println("  |");
			System.out.println("__|_____________");
			break;
		  case 5:
			System.out.println("  |---------");
            System.out.println("  |        |");
			System.out.println("  |        0");
			System.out.println("  |        |");
			System.out.println("  |        |");
			System.out.println("  |        |");
			System.out.println("  |");
			System.out.println("  |");
			System.out.println("__|_____________");
			break;
		  case 4:
			System.out.println("  |---------");
            System.out.println("  |        |");
			System.out.println("  |        0");
			System.out.println("  |        |");
			System.out.println("  |        |");
			System.out.println("  |        |");
			System.out.println("  |       |");
			System.out.println("  |      |");
			System.out.println("__|_____________");
			break;
		  case 3:
			System.out.println("  |---------");
            System.out.println("  |        |");
			System.out.println("  |        0");
			System.out.println("  |        |");
			System.out.println("  |        |");
			System.out.println("  |        |");
			System.out.println("  |       | | ");
			System.out.println("  |      |   |  ");
			System.out.println("__|_____________");
			break;
		   case 2:
		    System.out.println("  |---------");
            System.out.println("  |        |");
			System.out.println("  |        0");
			System.out.println("  |        | | ");
			System.out.println("  |        |  | ");
			System.out.println("  |        |");
			System.out.println("  |       | | ");
			System.out.println("  |      |   | ");
			System.out.println("__|_____________");
			break;
			case 1:
		    System.out.println("  |---------");
            System.out.println("  |        |");
			System.out.println("  |        0");
			System.out.println("  |      | | | ");
			System.out.println("  |     |  |  | ");
			System.out.println("  |        |");
			System.out.println("  |       | | ");
			System.out.println("  |      |   | ");
			System.out.println("__|_____________");
			break; 
		}
	}

	
	protected void askForGuess(String response) {
		String[] tmp = response.split("@"); 
		String word = tmp[0];
		int remain = Integer.parseInt(tmp[1]);
		draw_man(remain);
		System.out.println("Guess the word: " + word);
		while (true) {
			System.out.print("Enter a character: ");
			try {
				String userInput = this.consoleReader.readLine();
				if (userInput == null) {
					stopGame();
					break;
				} else {
					userInput = userInput.trim().toLowerCase();
					if (!userInput.matches("^[A-Za-z0-9]$")) {
						System.out.println("Invalid input. Type only one letter or number!");
					} else {
						writeToServer(userInput);
						this.tries++;
						break;
					}
				}
			} catch (IOException ex) {
				ex.printStackTrace();
				stopGame();
				break;
			}
		}
	}

	
	protected void gameWonOrLost(String response) {
		String won = response.substring(0, response.indexOf(":"));
		String word = response.substring(response.indexOf(":") + 1);
		System.out.print("You " + won.toLowerCase() + "! ");
		System.out.println("The word was '" + word + "'.\n");
		FileUtils.printToFile(this.out, word, (won.equals("WON") ? "Yes" : "No"), this.tries);
	}

	protected void gameOver() {
		cleanUp();
		System.out.println("Sorry, no more games!");
	}

	protected void handleFatalException(Exception ex) {
		ex.printStackTrace();
		System.err.println("A fatal problem occurred. Closing the game now...");
		closeConnection();
	}

	
	protected abstract void writeToServer(String msg);

	
	protected void cleanUp() {
		try {
			this.consoleReader.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	
	protected void stopGame() {
		writeToServer(Server.GAME_STOP);
		cleanUp();
	}

	protected abstract void closeConnection();
}
