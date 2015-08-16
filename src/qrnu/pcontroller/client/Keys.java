package qrnu.pcontroller.client;

public class Keys {

	private Key[] arrows;

	private Key[] characters;

	private Key[] specials;

	private Key[] numbers;

	class Key {

		private int keyEvent;
		private String name;

		public Key(int keyEvent, String name) {
			this.name = name;
			this.keyEvent = keyEvent;
		}

		public String getName() {
			return name;
		}

		public int getKeyEvent() {
			return keyEvent;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	public Keys() {
		int i = 0;
		arrows = new Key[4];
		characters = new Key[35];
		specials = new Key[45];
		numbers = new Key[25];

		characters[i] = new Key(65, "A");
		i++;
		characters[i] = new Key(66, "B");
		i++;
		characters[i] = new Key(67, "C");
		i++;
		characters[i] = new Key(68, "D");
		i++;
		characters[i] = new Key(69, "E");
		i++;
		characters[i] = new Key(70, "F");
		i++;
		characters[i] = new Key(71, "G");
		i++;
		characters[i] = new Key(72, "H");
		i++;
		characters[i] = new Key(73, "I");
		i++;
		characters[i] = new Key(74, "J");
		i++;
		characters[i] = new Key(75, "K");
		i++;
		characters[i] = new Key(76, "L");
		i++;
		characters[i] = new Key(77, "M");
		i++;
		characters[i] = new Key(78, "N");
		i++;
		characters[i] = new Key(79, "O");
		i++;
		characters[i] = new Key(80, "P");
		i++;
		characters[i] = new Key(81, "Q");
		i++;
		characters[i] = new Key(82, "R");
		i++;
		characters[i] = new Key(83, "S");
		i++;
		characters[i] = new Key(84, "T");
		i++;
		characters[i] = new Key(85, "U");
		i++;
		characters[i] = new Key(86, "V");
		i++;
		characters[i] = new Key(87, "W");
		i++;
		characters[i] = new Key(88, "X");
		i++;
		characters[i] = new Key(89, "Y");
		i++;
		characters[i] = new Key(90, "Z");
		i++;

		i = 0;
		arrows[i] = new Key(37, "Left Arrow");
		i++;
		arrows[i] = new Key(39, "Right Arrow");
		i++;
		arrows[i] = new Key(38, "Up Arrow");
		i++;
		arrows[i] = new Key(40, "Down Arrow");
		i++;

		i = 0;
		numbers[i] = new Key(48, "0");
		i++;
		numbers[i] = new Key(49, "1");
		i++;
		numbers[i] = new Key(50, "2");
		i++;
		numbers[i] = new Key(51, "3");
		i++;
		numbers[i] = new Key(52, "4");
		i++;
		numbers[i] = new Key(53, "5");
		i++;
		numbers[i] = new Key(54, "6");
		i++;
		numbers[i] = new Key(55, "7");
		i++;
		numbers[i] = new Key(56, "8");
		i++;
		numbers[i] = new Key(57, "9");
		i++;
		numbers[i] = new Key(96, "Numpad 0");
		i++;
		numbers[i] = new Key(97, "Numpad 1");
		i++;
		numbers[i] = new Key(98, "Numpad 2");
		i++;
		numbers[i] = new Key(99, "Numpad 3");
		i++;
		numbers[i] = new Key(100, "Numpad 4");
		i++;
		numbers[i] = new Key(101, "Numpad 5");
		i++;
		numbers[i] = new Key(102, "Numpad 6");
		i++;
		numbers[i] = new Key(103, "Numpad 7");
		i++;
		numbers[i] = new Key(104, "Numpad 8");
		i++;
		numbers[i] = new Key(105, "Numpad 9");
		i++;

		i = 0;
		specials[i] = new Key(17, "Ctrl");
		i++;
		specials[i] = new Key(18, "Alt");
		i++;
		specials[i] = new Key(16, "Shift");
		i++;
		specials[i] = new Key(32, "Space");
		i++;
		specials[i] = new Key(9, "Tab");
		i++;
		specials[i] = new Key(10, "Enter");
		i++;
		specials[i] = new Key(27, "Esc");
		i++;
		specials[i] = new Key(20, "Caps Lock");
		i++;
		specials[i] = new Key(524, "Win");
		i++;
		specials[i] = new Key(8, "Backspace");
		i++;
		specials[i] = new Key(59, ";");
		i++;
		specials[i] = new Key(222, "'");
		i++;
		specials[i] = new Key(44, ",");
		i++;
		specials[i] = new Key(46, ".");
		i++;
		specials[i] = new Key(47, "/");
		i++;
		specials[i] = new Key(45, "-");
		i++;
		specials[i] = new Key(61, "=");
		i++;
		specials[i] = new Key(91, "[");
		i++;
		specials[i] = new Key(93, "]");
		i++;
		specials[i] = new Key(92, "\\");
		i++;
		specials[i] = new Key(112, "F1");
		i++;
		specials[i] = new Key(113, "F2");
		i++;
		specials[i] = new Key(114, "F3");
		i++;
		specials[i] = new Key(115, "F4");
		i++;
		specials[i] = new Key(116, "F5");
		i++;
		specials[i] = new Key(117, "F6");
		i++;
		specials[i] = new Key(118, "F7");
		i++;
		specials[i] = new Key(119, "F8");
		i++;
		specials[i] = new Key(120, "F9");
		i++;
		specials[i] = new Key(121, "F10");
		i++;
		specials[i] = new Key(122, "F11");
		i++;
		specials[i] = new Key(123, "F12");
		i++;
		specials[i] = new Key(115, "Insert");
		i++;
		specials[i] = new Key(127, "Delete");
		i++;
		specials[i] = new Key(36, "Home");
		i++;
		specials[i] = new Key(35, "End");
		i++;
		specials[i] = new Key(33, "Page Up");
		i++;
		specials[i] = new Key(34, "Page Down");
		i++;
		specials[i] = new Key(144, "Num Lock");
		i++;

	}

	public Key[] getArrows() {
		return arrows;
	}

	public Key[] getCharacters() {
		return characters;
	}

	public Key[] getSpecials() {
		return specials;
	}

	public Key[] getNumbers() {
		return numbers;
	}

	public static String[] getNames(Key[] keys) {
		int max;
		for (max = 1; max < keys.length; max++) {
			try {
				keys[max - 1].toString();
			} catch (NullPointerException e) {
				max -= 1;
				break;
			}
		}
		String[] rtrn = new String[max];
		for (int i = 0; i < max; i++) {
			rtrn[i] = keys[i].toString();
		}
		return rtrn;
	}

	public String getName(int keyEvent) {
		for (int i = 0; i < arrows.length; i++) {
			try {
				if (arrows[i].getKeyEvent() == keyEvent) {
					return arrows[i].getName();
				}
			} catch (Exception e) {
				break;
			}
		}
		for (int i = 0; i < characters.length; i++) {
			try {
				if (characters[i].getKeyEvent() == keyEvent) {
					return characters[i].getName();
				}
			} catch (Exception e) {
				break;
			}
		}

		for (int i = 0; i < numbers.length; i++) {
			try {
				if (numbers[i].getKeyEvent() == keyEvent) {
					return numbers[i].getName();
				}
			} catch (Exception e) {
				break;
			}
		}

		for (int i = 0; i < specials.length; i++) {
			try {
				if (specials[i].getKeyEvent() == keyEvent) {
					return specials[i].getName();
				}
			} catch (Exception e) {
				break;
			}
		}

		return null;
	}

	public int getKeyEvent(String name) {
		for (int i = 0; i < arrows.length; i++) {
			try {
				if (arrows[i].getName().equals(name)) {
					return arrows[i].getKeyEvent();
				}
			} catch (Exception e) {
				break;
			}
		}
		for (int i = 0; i < characters.length; i++) {
			try {
				if (characters[i].getName().equals(name)) {
					return characters[i].getKeyEvent();
				}
			} catch (Exception e) {
				break;
			}
		}

		for (int i = 0; i < numbers.length; i++) {
			try {
				if (numbers[i].getName().equals(name)) {
					return numbers[i].getKeyEvent();
				}
			} catch (Exception e) {
				break;
			}
		}

		for (int i = 0; i < specials.length; i++) {
			try {
				if (specials[i].getName().equals(name)) {
					return specials[i].getKeyEvent();
				}
			} catch (Exception e) {
				break;
			}
		}

		return -5;
	}

}
