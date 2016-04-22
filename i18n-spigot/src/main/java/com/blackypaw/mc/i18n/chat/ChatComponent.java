package com.blackypaw.mc.i18n.chat;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for chat components as used for deserialization of JSON serialized chat messages.
 * Note, that this class does only support the most rudimentary features of the JSON chat system
 * as there is no need to decode colors and / or formatting for I18N's needs.
 *
 * @author BlackyPaw
 * @version 1.0
 */
public abstract class ChatComponent {

	private List<ChatComponent> extra;

	protected ChatComponent() {
		this.extra = new ArrayList<>( 0 );
	}

	/**
	 * Adds a child component to this component.
	 *
	 * @param component The component to be added as a child
	 */
	public void addChild( ChatComponent component ) {
		this.extra.add( component );
	}

	/**
	 * Builds the raw, unformatted chat message represented by this chat component and its children.
	 *
	 * @return The raw, unformatted chat message represented by this chat component
	 */
	public String getUnformattedText() {
		StringBuilder builder = new StringBuilder();
		this.createUnformattedText( builder );
		return builder.toString();
	}

	/**
	 * Internal builder method for unformatted text.
	 *
	 * @param builder The builder to use for appending text
	 */
	protected void createUnformattedText( StringBuilder builder ) {
		for ( ChatComponent component : this.extra ) {
			component.createUnformattedText( builder );
		}
	}

}
