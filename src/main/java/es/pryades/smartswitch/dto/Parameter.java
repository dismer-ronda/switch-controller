package es.pryades.smartswitch.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/

@EqualsAndHashCode(callSuper=true)
@Data
public class Parameter extends BaseDto
{
	private static final long serialVersionUID = 1113547474378950069L;
	
	public static final long PAR_DEFAULT_PAGE_SIZE			= 1;

	public static final long PAR_LOGIN_FAILS_NEW_PASS		= 2;
	public static final long PAR_LOGIN_FAILS_BLOCK			= 3;
	public static final long PAR_PASSWORD_MIN_SIZE			= 4;
	public static final long PAR_PASSWORD_VALID_TIME		= 5;
	public static final long PAR_MAIL_HOST_ADDRESS			= 6;
	public static final long PAR_MAIL_SENDER_EMAIL			= 7;
	public static final long PAR_MAIL_SENDER_USER			= 8;
	public static final long PAR_MAIL_SENDER_PASSWORD		= 9;

	public static final long PAR_HTTP_PROXY_HOST						= 10;
	public static final long PAR_HTTP_PROXY_PORT						= 11;
	
	public static final long PAR_SOCKS5_PROXY_HOST						= 12;
	public static final long PAR_SOCKS5_PROXY_PORT						= 13;

	public static final long PAR_STRENGTH_SIZE 							= 14;
	public static final long PAR_STRENGTH_CAPITAL						= 15;
	public static final long PAR_STRENGTH_DIGIT							= 16;
	public static final long PAR_STRENGTH_SYMBOL						= 17;
	public static final long PAR_STRENGTH_LOGIN							= 18;
	public static final long PAR_STRENGTH_REUSE							= 19;

	public static final long PAR_CHARTS_WIDTH 							= 20;
	public static final long PAR_CHARTS_HEIGHT							= 21;

	public static final long PAR_MAX_ROWS_EXPORTED						= 22;

	public static final long PAR_LOG_DEFAULT							= 23;
	
	private String description;
	private String value;
	private Integer display_order;
}
