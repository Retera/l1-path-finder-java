package net.warsmash.l1.pathfinder.util;

public class GridUtil {
	public static int[][] transpose(int[][] array) {
		int originalWidth = array.length;
		int originalHeight = array[0].length;
		int[][] transposedArray = new int[originalHeight][originalWidth];
		for (int i = 0; i < originalWidth; i++) {
			for (int j = 0; j < originalHeight; j++) {
				transposedArray[j][i] = array[i][j];
			}
		}
		return transposedArray;
	}
}
