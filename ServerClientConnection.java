public abstract class ServerClientConnection {
	
	protected int gamesCounter;

	public ServerClientConnection() {
		this.gamesCounter = 0;
	}

	public abstract void dealWithClient();
	
	protected boolean handleRequest(String request) {
		if (request.equals(Server.GAME_PLAY)) {
			if (this.gamesCounter == Server.hangmanGames.size()) {
				writeToClient(Server.GAME_OVER);
				return false;
			} else {
				String word = Server.hangmanGamesList.get(this.gamesCounter);
				int tries = Server.hangmanGames.get(word);
				Hangman game = new Hangman(word, tries);
				playThisGame(game);
				this.gamesCounter++;
				return true;
			}
		} else if (request.equals(Server.GAME_STOP)) {
			return false;
		}
		return false;
	}

	protected boolean checkGuess(Hangman game, String input) {
		if (input != null && input.length() > 0) {
			input = input.trim();
			if (input.equals(Server.GAME_STOP)) {
				cleanUp();
			} else if (input.length() == 1) {
				char c = input.charAt(0);
				game.guess(c);
				return true;
			}
		}
		return false;
	}

	protected void playThisGame(Hangman game) {
		if (game.won()) {
			writeToClient(Server.GAME_WON + ":" + game.getWord());
			// writeToClient(String.valueOf(game.getRemainingTries()));
		} else if (game.lost()) {
			writeToClient(Server.GAME_LOST + ":" + game.getWord());
			// writeToClient(String.valueOf(game.getRemainingTries()));
		}
	}

	protected void handleConnectionException(Exception ex) {
		ex.printStackTrace();
		System.err.println("A problem occurred. Closing connection with Client now...");
		closeClientConnection();
	}

	protected abstract void closeClientConnection();

	protected abstract void writeToClient(String msg);

	protected abstract void cleanUp();
}
