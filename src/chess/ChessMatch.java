package chess;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.Bishop;
import chess.pieces.King;
import chess.pieces.Knight;
import chess.pieces.Pawn;
import chess.pieces.Queen;
import chess.pieces.Rook;

public class ChessMatch {

	private int turn;
	private Color currentPlayer;
	private Board board;
	private boolean check;
	private boolean checkMate;
	private ChessPiece enPassantVulnerable;
	private ChessPiece promoted;
	
	private List<Piece> piecesOnTheBoard = new ArrayList<>();
	private List<Piece> capturedPieces = new ArrayList<>();
	
	public ChessMatch() {
		this.turn = 1;
		this.currentPlayer = Color.WHITE;
		this.board = new Board(8, 8);
		this.initialSetup();
	}
	
	public int getTurn() {
		return turn;
	}
	
	public Color getCurrentPlayer() {
		return currentPlayer;
	}
	
	public boolean isCheck() {
		return check;
	}
	
	public boolean isCheckMate() {
		return checkMate;
	}
	
	public ChessPiece getEnPassantVulnerable() {
		return enPassantVulnerable;
	}
	
	public ChessPiece getPromoted() {
		return promoted;
	}
	
	public ChessPiece[][] getPieces() {
		ChessPiece[][] mat = 
			new ChessPiece[this.board.getRows()][this.board.getColumns()];
		
		for (int i = 0; i< this.board.getRows(); i++) {
			for (int j = 0; j < this.board.getColumns(); j++) {
				mat[i][j] = (ChessPiece) this.board.piece(i, j);
			}
		}
		return mat;
	}
	
	public boolean[][] possibleMoves(ChessPosition sourcePosition){
		Position position = sourcePosition.toPosition();
		this.validateSourcePosition(position);
		return this.board.piece(position).possibleMoves();
	}
	
	public ChessPiece performChessMove(
		ChessPosition sourcePosition,
		ChessPosition targetPosition
	) {
		Position source = sourcePosition.toPosition();
		Position target = targetPosition.toPosition();
		
		this.validateSourcePosition(source);
		this.validateTargetPosition(source, target);
		
		Piece capturedPiece = this.makeMove(source, target);
		
		if (this.testCheck(this.currentPlayer)) {
			this.undoMove(source, target, capturedPiece);
			throw new ChessException("You cant put yoursef in check");
		}
		
		ChessPiece movedPiece = (ChessPiece) this.board.piece(target);
		
		// Special move promotion
		this.promoted = null;
		if(movedPiece instanceof Pawn) {
			if(
				movedPiece.getColor() == Color.WHITE 
				&& target.getRow() == 0 
				
				|| movedPiece.getColor() == Color.BLACK 
				&& target.getRow() == 7
			) {
				this.promoted = (ChessPiece) this.board.piece(target);
				this.promoted = this.replacePromotedPiece("Q");
			}
		}
		
		this.check = 
			(this.testCheck(opponent(this.currentPlayer))) ? true : false;
		
		if(testCheckMate(opponent(this.currentPlayer))) {
			this.checkMate = true;
		} else {
			this.nextTurn();
		}
		
		// Specialmove en passant
		if(movedPiece instanceof Pawn 
			&& (
			target.getRow() == source.getRow()  - 2 
			|| target.getRow() == source.getRow()  + 2
			)
		) {
			this.enPassantVulnerable = movedPiece;
		} else {
			this.enPassantVulnerable = null;
		}
		
		return (ChessPiece)capturedPiece;
	}
	
	public ChessPiece replacePromotedPiece(String type) {
		if(this.promoted == null) {
			throw new IllegalStateException("There is no piece to be promoted");
		}
		if(!type.equals("B")
			&&!type.equals("N")
			&&!type.equals("R")
			&&!type.equals("Q")) {
			throw new InvalidParameterException("Invalid type for promotion");
		}
		
		Position pos = this.promoted.getChessPosition().toPosition();
		Piece p = this.board.removePiece(pos);
		this.piecesOnTheBoard.remove(p);
		
		ChessPiece newPiece = this.newPiece(type, this.promoted.getColor());
		this.board.placePiece(newPiece, pos);
		this.piecesOnTheBoard.add(newPiece);
		
		return newPiece;
		
	}
	
	private ChessPiece newPiece(String type, Color color) {
		if(type.equals("B")) return new Bishop(this.board, color);
		if(type.equals("N")) return new Knight(this.board, color);
		if(type.equals("Q")) return new Queen(this.board, color);
		return new Rook(this.board, color);
	}

	private Piece makeMove(Position source, Position target) {
		ChessPiece p = (ChessPiece) this.board.removePiece(source);
		p.increaseMoveCount();
		Piece capturedPiece = board.removePiece(target);
		this.board.placePiece(p, target);
		
		if(capturedPiece != null) {
			this.piecesOnTheBoard.remove(capturedPiece);
			this.capturedPieces.add(capturedPiece);
		}
		
		// Specialmove castling kingside rook
		if(p instanceof King && target.getColumn() == source.getColumn() + 2) {
			Position sourceR = new Position(
				source.getRow(), source.getColumn() + 3
			);
			Position targetR = new Position(
				source.getRow(), source.getColumn() + 1
			);
			
			ChessPiece rook = (ChessPiece) this.board.removePiece(sourceR);
			this.board.placePiece(rook, targetR);
			rook.increaseMoveCount();
 		}
		
		// Specialmove castling queenside rook
		if(p instanceof King && target.getColumn() == source.getColumn() - 2) {
			Position sourceR = new Position(
				source.getRow(), source.getColumn() - 4
			);
			Position targetR = new Position(
				source.getRow(), source.getColumn() - 1
			);
			
			ChessPiece rook = (ChessPiece) this.board.removePiece(sourceR);
			this.board.placePiece(rook, targetR);
			rook.increaseMoveCount();
 		}
		
		// Special move en v
		if(p instanceof Pawn) {
			if(source.getColumn() != target.getColumn() 
				&& capturedPiece == null) {
				Position pawnPosition;
				if(p.getColor() == Color.WHITE) {
					pawnPosition = new Position(
						target.getRow() + 1, target.getColumn()
					);
				} else {
					pawnPosition = new Position(
						target.getRow() - 1, target.getColumn()
					);
				}
				capturedPiece = this.board.removePiece(pawnPosition);
				this.capturedPieces.add(capturedPiece);
				this.piecesOnTheBoard.remove(capturedPiece);
			}
		}
		
		return capturedPiece;
	}
	
	private void undoMove(
		Position source,
		Position target,
		Piece capturedPiece
	) {
		ChessPiece p = (ChessPiece) this.board.removePiece(target);
		p.decreaseMoveCount();
		this.board.placePiece(p, source);
		
		if(capturedPiece != null) {
			this.board.placePiece(capturedPiece, target);
			
			this.capturedPieces.remove(capturedPiece);
			this.piecesOnTheBoard.add(capturedPiece);
		}
		
		// Specialmove castling kingside rook
		if(p instanceof King && target.getColumn() == source.getColumn() + 2) {
			Position sourceR = new Position(
				source.getRow(), source.getColumn() + 3
			);
			Position targetR = new Position(
				source.getRow(), source.getColumn() + 1
			);
			
			ChessPiece rook = (ChessPiece) this.board.removePiece(targetR);
			this.board.placePiece(rook, sourceR);
			rook.decreaseMoveCount();
 		}
		
		// Specialmove castling queenside rook
		if(p instanceof King && target.getColumn() == source.getColumn() - 2) {
			Position sourceR = new Position(
				source.getRow(), source.getColumn() - 4
			);
			Position targetR = new Position(
				source.getRow(), source.getColumn() - 1
			);
			
			ChessPiece rook = (ChessPiece) this.board.removePiece(sourceR);
			this.board.placePiece(rook, sourceR);
			rook.decreaseMoveCount();
 		}
		
		// Special move en pasant
		if(p instanceof Pawn) {
			if(source.getColumn() != target.getColumn() 
				&& capturedPiece == this.enPassantVulnerable) {
				ChessPiece pawn = (ChessPiece) this.board.removePiece(target);
				Position pawnPosition;
				if(p.getColor() == Color.WHITE) {
					pawnPosition = new Position(3, target.getColumn()
					);
				} else {
					pawnPosition = new Position(4, target.getColumn()
					);
				}
				this.board.placePiece(pawn, pawnPosition);
			}
		}
	}
	
	private void validateSourcePosition(Position position) {
		if (!this.board.thereIsAPiece(position)) {
			throw new ChessException("There is no piece on source position");
		}
		if(this.currentPlayer 
			!= ((ChessPiece) this.board.piece(position)).getColor()) {
			throw new ChessException(
				"The chosen piece is not yours"
			);
		}
		if(!this.board.piece(position).isThereAnyPossibleMove()) {
			throw new ChessException(
				"There is no posible moves for the chosen piece"
			);
		}
	}
	
	private void validateTargetPosition(Position source, Position target) {
		if(!this.board.piece(source).possibleMove(target)) {
			throw new ChessException(
				"The chosen piece can't move to target position"
			);
		}
	}
	
	private void nextTurn() {
		this.turn++;
		this.currentPlayer = 
			(this.currentPlayer == Color.WHITE) ? Color.BLACK : Color.WHITE; 
	}
	
	private Color opponent(Color color) {
		return (color == Color.WHITE) ? Color.BLACK : Color.WHITE;
	}
	
	private ChessPiece king(Color color) {
		List<Piece> list = this.piecesOnTheBoard.stream().filter(
			x -> ((ChessPiece)x).getColor() == color
		).collect(Collectors.toList());
		
		for(Piece p : list) {
			if(p instanceof King) {
				return (ChessPiece) p;
			}
		}
		
		throw new IllegalStateException(
			"There is no " + color + " King on the board"
		);
	}
	
	private boolean testCheck(Color color) {
		Position kingPosition = this.king(color).getChessPosition().toPosition();
		List<Piece> opponentPieces = this.piecesOnTheBoard.stream().filter(
				x -> ((ChessPiece)x).getColor() == this.opponent(color)
			).collect(Collectors.toList());
		
		for(Piece p : opponentPieces) {
			boolean[][] possibleMoves = p.possibleMoves();
			if(possibleMoves[kingPosition.getRow()][kingPosition.getColumn()]) {
				return true;
			}
		}
		
		return false;
	}
	
	private boolean testCheckMate(Color color) {
		if(!testCheck(color)) {
			return false;
		}
		
		List<Piece> list = this.piecesOnTheBoard.stream().filter(
				x -> ((ChessPiece)x).getColor() == color
			).collect(Collectors.toList());
		
		for(Piece p : list) {
			boolean[][] possibleMoves = p.possibleMoves();
			
			for(int i = 0; i < this.board.getRows(); i++) {
				for(int j = 0; j < this.board.getColumns(); j++) {
					
					if (possibleMoves[i][j]) {
						Position source = 
							((ChessPiece) p).getChessPosition().toPosition();
						
						Position target = new Position(i, j);
						Piece capturedPiece = this.makeMove(source, target);
						boolean testCheck = this.testCheck(color);
						this.undoMove(source, target, capturedPiece);
						
						if(!testCheck) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}
	
	private void placeNewPiece(char column, int row, ChessPiece piece) {
		this.board.placePiece(piece, new ChessPosition(column, row).toPosition());
		this.piecesOnTheBoard.add(piece);
	}
	
	private void initialSetup() {
		// White pieces
		placeNewPiece('a', 1, new Rook(board, Color.WHITE));
        placeNewPiece('b', 1, new Knight(board, Color.WHITE));
        placeNewPiece('c', 1, new Bishop(board, Color.WHITE));
        placeNewPiece('d', 1, new Queen(board, Color.WHITE));
        placeNewPiece('e', 1, new King(board, Color.WHITE, this));
        placeNewPiece('f', 1, new Bishop(board, Color.WHITE));
        placeNewPiece('g', 1, new Knight(board, Color.WHITE));
        placeNewPiece('h', 1, new Rook(board, Color.WHITE));
        placeNewPiece('a', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('b', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('c', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('d', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('e', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('f', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('g', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('h', 2, new Pawn(board, Color.WHITE, this));
        
		// Black pieces
        placeNewPiece('a', 8, new Rook(board, Color.BLACK));
        placeNewPiece('b', 8, new Knight(board, Color.BLACK));
        placeNewPiece('c', 8, new Bishop(board, Color.BLACK));
        placeNewPiece('d', 8, new Queen(board, Color.BLACK));
        placeNewPiece('e', 8, new King(board, Color.BLACK, this));
        placeNewPiece('f', 8, new Bishop(board, Color.BLACK));
        placeNewPiece('g', 8, new Knight(board, Color.BLACK));
        placeNewPiece('h', 8, new Rook(board, Color.BLACK));
        placeNewPiece('a', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('b', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('c', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('d', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('e', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('f', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('g', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('h', 7, new Pawn(board, Color.BLACK, this));
	}
}
