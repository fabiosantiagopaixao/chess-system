package chess.pieces;

import boardgame.Board;
import boardgame.Position;
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

	private boolean canMove(Position position) {
		ChessPiece p = (ChessPiece) this.getBoard().piece(position);
		return p == null || p.getColor() != this.getColor();
	}

	@Override
	public boolean[][] posibleMoves() {
		boolean[][] mat = 
			new boolean[this.getBoard().getRows()][this.getBoard().getColumns()];
		
		Position p = new Position(0, 0);

		// Above
		p.setValues(this.position.getRow() - 1, this.position.getColumn());
		if (this.getBoard().positionExists(p) && this.canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}

		// Below
		p.setValues(this.position.getRow() + 1, this.position.getColumn());
		if (this.getBoard().positionExists(p) && this.canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}

		// Left
		p.setValues(this.position.getRow(), this.position.getColumn() - 1);
		if (this.getBoard().positionExists(p) && this.canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}

		// right
		p.setValues(this.position.getRow(), this.position.getColumn() + 1);
		if (this.getBoard().positionExists(p) && this.canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}

		// nw
		p.setValues(this.position.getRow() - 1, this.position.getColumn() - 1);
		if (this.getBoard().positionExists(p) && this.canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}

		// ne
		p.setValues(this.position.getRow() - 1, this.position.getColumn() + 1);
		if (this.getBoard().positionExists(p) && this.canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}

		// sw
		p.setValues(this.position.getRow() + 1, this.position.getColumn() - 1);
		if (this.getBoard().positionExists(p) && this.canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}

		// se
		p.setValues(this.position.getRow() + 1, this.position.getColumn() + 1);
		if (this.getBoard().positionExists(p) && this.canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}

		return mat;
	}

}
