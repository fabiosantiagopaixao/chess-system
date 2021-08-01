package chess;

import boardgame.Board;

public class ChessMach {

	private Board board;

	public ChessMach() {
		this.board = new Board(8, 8);
	}

	public ChessPiece[][] getPieces() {
		ChessPiece[][] pieces = 
				new ChessPiece[this.board.getRows()][this.board.getColumns()];

		for (int i = 0; i < this.board.getRows(); i++) {
			for (int j = 0; j < this.board.getColumns(); j++) {
				pieces[i][j] = (ChessPiece) this.board.piece(i, j);
			}
		}

		return pieces;
	}

}
