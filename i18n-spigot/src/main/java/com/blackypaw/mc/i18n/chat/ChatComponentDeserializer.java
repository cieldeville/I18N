/*
 * Copyright (c) 2016, BlackyPaw
 * All rights reserved.
 *
 * This code is licensed under a BSD 3-Clause license. For further license details view the LICENSE file in the root folder of this source tree.
 */

package com.blackypaw.mc.i18n.chat;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

import java.lang.reflect.Type;

/**
 * This class provides a Gson deserializer that converts JSON formatted chat messages back into
 * chat components.
 *
 * @author BlackyPaw
 * @version 1.0
 */
public class ChatComponentDeserializer implements JsonDeserializer<ChatComponent> {
	
	@Override
	public ChatComponent deserialize( JsonElement json, Type type, JsonDeserializationContext ctx ) throws JsonParseException {
		return this.deserializeComponent( json );
	}

	/**
	 * Detects the type of the given json element and deserializes its contents into the appropriate
	 * component types.
	 *
	 * @param json The json element to be deserialized
	 *
	 * @return The deserialized chat component
	 */
	private ChatComponent deserializeComponent( JsonElement json ) {
		if ( json.isJsonPrimitive() ) {
			// Only text components can be raw primitives (strings):
			return this.deserializeTextComponent( json );
		} else if ( json.isJsonArray() ) {
			// Only text components can be json arrays:
			return this.deserializeComponentArray( json.getAsJsonArray() );
		} else {
			return this.deserializeTextComponent( json );
		}
	}

	/**
	 * Deserializes a JSON formatted component array, e.g. '["Hey, ",{extra:["there!"]}]'
	 *
	 * @param json The JSON array to be deserialized into a chat component
	 *
	 * @return The deserialized chat component
	 */
	private ChatComponent deserializeComponentArray( JsonArray json ) {
		// Each and every element inside a component array is a text component
		// The first element of the array is considered to be the parent component:

		if ( json.size() <= 0 ) {
			return new TextComponent( "" );
		}

		final TextComponent parent = this.deserializeTextComponent( json.get( 0 ) );
		for ( int i = 1; i < json.size(); ++i ) {
			parent.addChild( this.deserializeTextComponent( json.get( i ) ) );
		}

		return parent;
	}

	/**
	 * Creates a text component given its JSON representation. Both the shorthand notation as
	 * a raw string as well as the notation as a full-blown JSON object are supported.
	 *
	 * @param json The JSON element to be deserialized into a text component
	 *
	 * @return The deserialized TextComponent
	 */
	private TextComponent deserializeTextComponent( JsonElement json ) {
		final TextComponent component = new TextComponent( "" );

		if ( json.isJsonPrimitive() ) {
			JsonPrimitive primitive = json.getAsJsonPrimitive();
			if ( primitive.isString() ) {
				component.setText( primitive.getAsString() );
			}
		} else if ( json.isJsonObject() ) {
			JsonObject object = json.getAsJsonObject();

			if ( object.has( "text" ) ) {
				JsonElement textElement = object.get( "text" );
				if ( textElement.isJsonPrimitive() ) {
					JsonPrimitive textPrimitive = textElement.getAsJsonPrimitive();
					if ( textPrimitive.isString() ) {
						component.setText( textPrimitive.getAsString() );
					}
				}
			}

			if ( object.has( "extra" ) ) {
				JsonElement extraElement = object.get( "extra" );
				if ( extraElement.isJsonArray() ) {
					JsonArray extraArray = extraElement.getAsJsonArray();
					for ( int i = 0; i < extraArray.size(); ++i ) {
						JsonElement fieldElement = extraArray.get( i );
						component.addChild( this.deserializeComponent( fieldElement ) );
					}
				}
			}
		}

		return component;
	}

}