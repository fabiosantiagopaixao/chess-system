package chess.pieces;

import boardgame.Board;
import chess.ChessPiece;
import chess.Color;

public class Rook extends ChessPiece {

	public Rook(Board board, Color color) {
		super(board, color);
	}

	@Override
	public String toString() {
		return "R";
	}
	
	@Override
	public boolean[][] posibleMoves() {
		boolean[][] mat = 
			new boolean
				[this.getBoard().getRows()][this.getBoard().getColumns()];
		
		return mat;
	}

}
