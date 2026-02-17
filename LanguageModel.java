import java.util.HashMap;
import java.util.Random;

public class LanguageModel {

    // The map of this model.
    // Maps windows to lists of charachter data objects.
    HashMap<String, List> CharDataMap;
    
    // The window length used in this model.
    int windowLength;
    
    // The random number generator used by this model. 
	private Random randomGenerator;

    /** Constructs a language model with the given window length and a given
     *  seed value. Generating texts from this model multiple times with the 
     *  same seed value will produce the same random texts. Good for debugging. */
    public LanguageModel(int windowLength, int seed) {
        this.windowLength = windowLength;
        randomGenerator = new Random(seed);
        CharDataMap = new HashMap<String, List>();
    }

    /** Constructs a language model with the given window length.
     * Generating texts from this model multiple times will produce
     * different random texts. Good for production. */
    public LanguageModel(int windowLength) {
        this.windowLength = windowLength;
        randomGenerator = new Random();
        CharDataMap = new HashMap<String, List>();
    }

    /** Builds a language model from the text in the given file (the corpus). */
	public void train(String fileName) {
		String window = "";
        char c;
        In in = new In(fileName);
        for (int i = 0; i < windowLength; i++) {
         c = in.readChar();
        window += c; 
        }
        while (!in.isEmpty()) {
            c = in.readChar();
            
        if (!CharDataMap.containsKey(window)) {
            CharDataMap.put(window, new List());
            }
            List windowList = CharDataMap.get(window);
            windowList.update(c);
            window = window.substring(1) + c;
        }
        
        if (!CharDataMap.containsKey(window)) {
            CharDataMap.put(window, new List());
        }
        List lastWindowList = CharDataMap.get(window);
        lastWindowList.update(' ');
        for (List list : CharDataMap.values()) {
        calculateProbabilities(list);
    }
	}

    // Computes and sets the probabilities (p and cp fields) of all the
	// characters in the given list. */
	void calculateProbabilities(List probs) {				
	 int totalchars =0;
     ListIterator it= probs.listIterator(0);
     while (it.hasNext()) {
        CharData cuurdata =it .next();
        totalchars += cuurdata.count;
     }
     double sum= 0.0;
     it = probs.listIterator(0);
     while (it.hasNext()){
        CharData cuurdata =it.next();
        cuurdata.p= (double) cuurdata.count/totalchars;
        sum+= cuurdata.p;
        cuurdata.cp= sum;
     }
	}

    // Returns a random character from the given probabilities list.
	char getRandomChar(List probs) {
		double r = randomGenerator.nextDouble();
        ListIterator it = probs.listIterator(0);
        while( it.hasNext()){
             CharData cuurdata =it.next();
             if(cuurdata.cp>r){
                return cuurdata.chr;
             }

        }
		return ' ';
	}

    /**
	 * Generates a random text, based on the probabilities that were learned during training. 
	 * @param initialText - text to start with. If initialText's last substring of size numberOfLetters
	 * doesn't appear as a key in Map, we generate no text and return only the initial text. 
	 * @param numberOfLetters - the size of text to generate
	 * @return the generated text
	 */
	public String generate(String initialText, int textLength) {
		String window = initialText;
    
    if (window.length() > windowLength) {
        window = window.substring(window.length() - windowLength);
    }
    
    StringBuilder generatedText = new StringBuilder(initialText);

    for (int i =0; i <textLength; i++) {
    List windowList = CharDataMap.get(window);
        
        if (windowList == null) {
         break; 
        }
    char c =getRandomChar(windowList);
    generatedText.append(c);
        
    window =window.substring(1) + c; 
    }
    return generatedText.toString();
	}

    /** Returns a string representing the map of this language model. */
	public String toString() {
		StringBuilder str = new StringBuilder();
		for (String key : CharDataMap.keySet()) {
			List keyProbs = CharDataMap.get(key);
			str.append(key + " : " + keyProbs + "\n");
		}
		return str.toString();
	}

    public static void main(String[] args) {
		int windowLength = Integer.parseInt(args[0]);
        String initialText = args[1];
        int generatedTextLength = Integer.parseInt(args[2]);
        boolean randomGeneration = Boolean.parseBoolean(args[3]);
        String fileName = args[4];

        LanguageModel lm;
        
        if(randomGeneration) {
        lm =new LanguageModel(windowLength);
        } else {
        lm =new LanguageModel(windowLength, 20); 
        }
        lm.train(fileName);
        lm.generate(initialText, generatedTextLength);
    }
}
