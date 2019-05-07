public class Hangman {
	private String word;
	private String encodedWord;
	private int tries;
	private int remainingTries;

	public Hangman(String word, int tries) {
		this.word = word;
		this.encodedWord = encodeWord(word);
		this.tries = tries;
		this.remainingTries = tries;
	}
	public void guess(char c) {
		boolean ok = false;
		StringBuilder encoded = new StringBuilder("");
		for (int i = 0; i < this.word.length(); i++) {
			if (Character.toLowerCase(this.word.charAt(i)) == Character.toLowerCase(c)) {
				encoded.append(this.word.charAt(i));
				ok = true;
			} else {
				encoded.append(this.encodedWord.charAt(i));
			}
		}
		this.encodedWord = encoded.toString();
		if (!ok) {
			--this.remainingTries;
		}
	}

	public boolean lost() {
		return this.remainingTries <= 0;
	}

	public boolean won() {
		return this.encodedWord.equalsIgnoreCase(this.word);
	}

	public String getWord() {
		return this.word;
	}

	public String getEncodedWord() {
		return this.encodedWord;
	}

	public int getTries() {
		return this.tries;
	}

	public int getRemainingTries() {
		return this.remainingTries;
	}

	private String encodeWord(String word) {
		StringBuilder encoded = new StringBuilder("");
		for (int i = 0; i < word.length(); i++) {
			encoded.append("-");
		}
		return encoded.toString();
	}
}
