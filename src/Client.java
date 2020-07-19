package encoder;

/*
 * [Client.java]
 * This program encodes the text in "ORIGINAL.txt" to "COMPRESSED.MZIP"
 * using the Huffman encoding technique
 * Matthew Sekirin
 * May 2nd, 2020
 */
public class Client {
	public static void main(String[] args) {
		Encoder file = new Encoder("ORIGINAL.txt");
	}
}
