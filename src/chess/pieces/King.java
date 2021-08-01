package chess.pieces;

import boardgame.Board;
import chess.ChessPiece;
import chess.Color;

public class King extends ChessPiece {

	public King(Board board, Color color) {
		super(board, color);
	}

	@Override
	public String toString() {
		return "K";
	}

	@Override
	public boolean[][] posibleMoves() {
		boolean[][] mat = 
			new boolean
				[this.getBoard().getRows()][this.getBoard().getColumns()];
		
		return mat;
	}

}
