package com.blackypaw.mc.i18n.chat;

/**
 * A chat component that contains some abitrary text.
 *
 * @author BlackyPaw
 * @version 1.0
 */
public class TextComponent extends ChatComponent {

	private String text;

	/**
	 * Constructs a new, empty chat component.
	 */
	public TextComponent() {
		this( "" );
	}

	/**
	 * Constructs a new chat component which will hold the given text.
	 *
	 * @param text The text this component should hold
	 */
	public TextComponent( String text ) {
		this.text = text;
	}

	/**
	 * Gets the text held by this text component.
	 *
	 * @return The text held by this text component
	 */
	public String getText() {
		return text;
	}

	/**
	 * Sets the text to be held by this text component. Must not be null.
	 *
	 * @param text The text to be held by this text component
	 */
	public void setText( String text ) {
		if ( text == null ) {
			throw new NullPointerException( "Cannot set text of TextComponent to null!" );
		}
		this.text = text;
	}

	@Override
	protected void createUnformattedText( StringBuilder builder ) {
		builder.append( this.text );
		super.createUnformattedText( builder );
	}

}