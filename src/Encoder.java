package encoder;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

/*
 * Encoder
 * This class provides the ability to encode a file. It maps the characters
 * of the file to the frequencies in which they appear and creates another mapping
 * that maps these same characters to their Huffman codes. It then encodes the file
 * by using instances of the inner classes RealEncoder and FileReadWrite 
 */
public class Encoder {
	private CustomHashMap<Character, String> codesMap;
	private CustomHashMap<Character, Integer> freqMap;
	private String tree;
	private RealEncoder coder;
	
	Encoder(String filename) {
		// read from file to be encoded
		FileReadWrite readWriter = new FileReadWrite();
		freqMap = new CustomHashMap<Character, Integer>();
		readWriter.loadFile(filename);

		Node root = createTree(freqMap);
		tree = getRep("", root); // will be second line of output

		this.codesMap = new CustomHashMap<Character, String>();
		getCodes(codesMap, root, ""); // builds the codesMap
		
		this.coder = new RealEncoder();
		readWriter.writeToFile(filename, "COMPRESSED.MZIP");
	}

	/*
	 * createTree
	 * This method creates a Huffman tree from freqMap
	 * @param freqMap, a CustomHashMap that maps the characters that appear in the text
	 * to the amount of times they appear
	 * @return queue.peek(), the remainingNode in queue representing the root of the Huffman tree
	 */
	private Node createTree(CustomHashMap<Character, Integer> freqMap) {
		CustomPQ<Node> queue = new CustomPQ<Node>();
		Iterator<Character> itr = freqMap.iterator();
		char key;
		while (itr.hasNext()) {
			key = itr.next();
			queue.add(new Node(Character.toString(key), freqMap.get(key)));
		}
		
		while (queue.size() != 1) {
			Node first = queue.dequeue();
			Node second = queue.dequeue();
			queue.add(new Node(first.getText() + second.getText(), first.getFreq() + second.getFreq(), first, second));
		}

		return queue.peek();
	}

	/*
	 * getCodes
	 * This method recursively builds up codesMap by traversing the (binary) Huffman tree
	 * @param codesMap, a CustomHashMap that maps the characters that appear in the text
	 * to their huffman codes
	 * @param node, the Node that represents our current position on the tree
	 * @param str, the "code" of the node we are at
	 */
	private void getCodes(CustomHashMap<Character, String> codesMap, Node node, String str) {
		if (node.isLeaf()) {
			// since the node is a leaf, we are not losing any information by using charAt(0)
			codesMap.put(node.getText().charAt(0), str); 
		} else {
			getCodes(codesMap, node.getLeft(), str + "0");
			getCodes(codesMap, node.getRight(), str + "1");
		}
	}
	
	/*
	 * getRep
	 * This method recursively creates the String representation of the Huffman tree
	 * @param tree, the String representation of the Huffman tree
	 * @param node, the Node that represents our current position on the tree
	 */
	private String getRep(String tree, Node node) {
		if (node.getLeft().isLeaf() && node.getRight().isLeaf()) {
			tree += "(" + node.getLeft().getText() + " " + node.getRight().getText() + ")";
			return tree;
		} else if (node.getLeft().isLeaf()) {
			return concatenate(node.getLeft().getText(), getRep(tree, node.getRight()));
		} else if (node.getRight().isLeaf()) {
			return concatenate(getRep(tree, node.getLeft()), node.getRight().getText());
		} else {
			return concatenate(getRep(tree, node.getLeft()), getRep(tree, node.getRight()));
		}
	}

	/*
	 * concatenate
	 * This method concatenates two Strings, surrounding them with round brackets
	 * @param a, the String that goes on the left
	 * @param b, the String that goes on the right
	 * @return the result of the concatenation
	 */
	private String concatenate(String a, String b) {
		return "(" + a + " " + b + ")";
	}

	/*
	 * Node
	 * This class represents a Node in the Huffman tree
	 */
	private class Node implements Comparable<Node> {
		private String text;
		private int freq;
		private Node left;
		private Node right;

		Node(String text, int freq) {
			this(text, freq, null, null);
		}

		Node(String text, int freq, Node left, Node right) {
			this.text = text;
			this.freq = freq;

			this.left = left;
			this.right = right;
		}
		
		/*
		 * compareTo
		 * This method compares two nodes by their frequency field
		 * @param node, the Node we are comparing to
		 */
		@Override
		public int compareTo(Node node) {
			if (this.freq < node.getFreq()) {
				return -1;
			} else if (this.freq == node.getFreq()) {
				return 0;
			} else {
				return 1;
			}
		}
		
		/*
		 * toString
		 * @return, returns a String representation of the Node
		 */
		@Override
		public String toString() {
			return text + " " + freq;
		}
		
		//-----------------------------------------------------------------------------
		// getter and setter methods
		public int getFreq() {
			return freq;
		}
		
		public String getText() {	
			return text;
		}
		
		public String getCode() {
			if (text.length() == 1) {
				return Integer.toString(text.charAt(0));
			} else {
				throw new RuntimeException("getCode() cannot be called on an object that does not represent a single character");
			}
		}

		public Node getLeft() {
			return this.left;
		}

		public void setLeft(String text, int freq) {
			this.left = new Node(text, freq);
		}

		public Node getRight() {
			return this.right;
		}

		public void setRight(String text, int freq) {
			this.right = new Node(text, freq);
		}
		//-----------------------------------------------------------------------------
		
		// helper method
		public boolean isLeaf() {
			if ((this.right == null) && (this.left == null)) {
				return true;
			} else {
				return false;
			}
		}
	}

	private class FileReadWrite {
		/*
		 * loadFile
		 * This method loads a file and builds up the freqMap which maps
		 * the characters to their respective frequencies
		 * @param filename, a String that represents the filename to be read from
		 */
		public void loadFile(String filename) {
			FileInputStream in = null;
			try {
				in = new FileInputStream(filename);
				int c = in.read();
				if (c == -1) {
					throw new IllegalArgumentException("Empty file");
				}
				
				char charForMap;
				do {
					charForMap = (char) c;
					if (freqMap.get(charForMap) == null) {
						freqMap.put(charForMap, 1);
					} else {
						freqMap.put(charForMap, freqMap.get(charForMap) + 1);
					}
				} while ((c = in.read()) != -1);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		/*
		 * writeToFile
		 * This method writes the fromName, String representation of the Huffman tree,
		 * padding, and encoded text (bytes representation) on separate lines to the file specified by toName.
		 * @param fromName, a String representing the filename that was being read from
		 * @param toName, a String representing the filename that is being written to
		 */
		public void writeToFile(String fromName, String toName) {
			FileOutputStream out = null;
			FileInputStream in = null;
			try {
				out = new FileOutputStream(toName);
				out.write(fromName.getBytes());
				out.write('\n');
				out.write(tree.getBytes());
				out.write('\n');
				
				/*
				 * The following is necessary in order to write the padding to a file before
				 * the encoded text. Despite appearances, this should not be problematic efficiency-wise.
				 */
				int c;
				int bitLength = 0;
				in = new FileInputStream(fromName);
				while ((c = in.read()) != -1) {
					bitLength += codesMap.get((char) c).length();
				}
				out.write(String.valueOf((bitLength % 8 == 0) ? 0 : (8 - bitLength % 8)).getBytes());
				out.write('\n');
				in.close();
				
				in = new FileInputStream(fromName);
				int max;
				while ((c = in.read()) != -1) {
					// characters are encoded one-by-one in case of large files
					max = coder.addToEncoded(Character.valueOf((char) c));
					for (int i = 0; i < max; i++) {
						out.write(coder.encoded[i]);
					}
						
					coder.reset();
				}
				if (coder.encoded[0] != 0) {
					out.write(coder.encoded[0]); // last byte
				}
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/*
	 * RealEncoder
	 * This class allows for the encoding of characters one at a time through the combination
	 * of addToEncoded() and reset()
	 */
	private class RealEncoder {
		private byte[] encoded;
		private int subIndex = 0;
		private int index;
		private int rightLen, length;
		private byte code;
		
		RealEncoder() {
			// length is number of unique chars / 8 bits in a byte
			encoded = new byte[freqMap.size() / 8 + 2]; // + 1 for length, + 1 in case partially filled already
		}
		
		/*
		 * This method writes the code of the character given as an argument to the encoded byte array
		 * @param c, the Character to be encoded
		 * @return index, an int where for all 0 <= i < index, encoded[i] was completely filled 
		 */
		public int addToEncoded(Character c) {
			index = 0;
			code = (byte) (toBinary(codesMap.get(c)));
			length = (byte) (codesMap.get(c).length());
			rightLen = 8 - subIndex;

			if (length <= rightLen) {
				encoded[index] =  (byte) ((code << (rightLen - length)) 
						| encoded[index]);
				if (length == rightLen) {
					index++;
				}
			} else {
				int shiftRight = length - rightLen;
				encoded[index] = (byte) ((code >>> shiftRight) | encoded[index]);
				index++;
				shiftRight -= 8;
				while (shiftRight >= 0) {
					encoded[index] = (byte) (code >>> shiftRight);
					
					index++;
					shiftRight -= 8;
				}
				
				encoded[index] = (byte) (code << (8 * (index + 1) - length - subIndex));
			}
	
			subIndex = (subIndex + length) % 8;

			return index;
		}
		
		/*
		 * reset
		 * This method creates a new encoded array of the same size with the first element equal to the
		 * element at index index.
		 */
		public void reset() {
			if (index > 0) {
				byte temp = encoded[index];
				encoded = new byte[freqMap.size() / 8 + 2];
				encoded[0] = temp;
			}
		}
		
		/*
		 * toBinary
		 * This method converts str to an integer
		 * @param str, the binary String that is being converted to an integer
		 */
		private int toBinary(String str) {
			int result = 0;
			for (int i = str.length() - 1; i >= 0; i--) {
				if (str.charAt(i) == '1') {
					result += Math.pow(2, str.length() - i - 1);
				}
			}

			return result;
		}
	}
}
