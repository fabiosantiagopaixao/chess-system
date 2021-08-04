package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.Color;

public class King extends ChessPiece {

	private ChessMatch chessMatch;
	
	public King(Board board, Color color, ChessMatch chessMatch) {
		super(board, color);
		this.chessMatch = chessMatch;
	}

	private boolean canMove(Position position) {
		ChessPiece p = (ChessPiece) this.getBoard().piece(position);
		return p == null || p.getColor() != this.getColor();
	}

	@Override
	public boolean[][] possibleMoves() {
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
		
		// Specialmove castling
		if(this.getMoveCount() == 0 && !this.chessMatch.isCheck()) {
			
			// Specialmove castling kingside rook
			Position posR1 = new Position(
				this.position.getRow(), this.position.getColumn() + 3
			);
			
			if(this.testRookCastling(posR1)) {
				Position p1 = new Position(
					this.position.getRow(), this.position.getColumn() + 1
				);
				Position p2 = new Position(
					this.position.getRow(), this.position.getColumn() + 2
				);
				
				if(this.getBoard().piece(p1) == null 
					&& this.getBoard().piece(p2) == null) {
					mat[this.position.getRow()]
						[this.position.getColumn() + 2] = true;
				}
			}
			
			// Specialmove castling queenside rook
			Position posR2 = new Position(
				this.position.getRow(), this.position.getColumn() - 4
			);
			
			if(this.testRookCastling(posR2)) {
				Position p1 = new Position(
					this.position.getRow(), this.position.getColumn() -1 
				);
				Position p2 = new Position(
					this.position.getRow(), this.position.getColumn() - 2
				);
				Position p3 = new Position(
					this.position.getRow(), this.position.getColumn() - 3
				);
				
				if(this.getBoard().piece(p1) == null 
					&& this.getBoard().piece(p2) == null
					&& this.getBoard().piece(p3) == null) {
					mat[this.position.getRow()]
						[this.position.getColumn() - 2] = true;
				}
			}
			
			
		}

		return mat;
	}
	
	private boolean testRookCastling(Position position) {
		ChessPiece p = (ChessPiece) this.getBoard().piece(position);
		
		return p != null 
			&& p instanceof Rook
			&& p.getColor() == this.getColor() 
			&& p.getMoveCount() == 0;
	}
	
	@Override
	public String toString() {
		return "K";
	}

}
