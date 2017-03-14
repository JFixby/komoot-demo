
package com.jfixby.komoot.qsq.separator;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.jfixby.scarabei.api.collections.Collections;
import com.jfixby.scarabei.api.collections.List;
import com.jfixby.scarabei.api.debug.Debug;
import com.jfixby.scarabei.api.log.L;

public class DigestEmail {

	private final String from;
	private final String to;
	private final String subject;
	final StringBuilder body = new StringBuilder();

	public DigestEmail (final DigestEmailSpecs specs) {
		this.from = specs.getFromEmailAdress();
		this.to = specs.getToEmailAdress();
		this.subject = specs.getSubject();

	}

	final List<Notification> list = Collections.newList();

	public void addNotification (final Notification notification) {
		Debug.checkNull("notification", notification);
		this.list.add(notification);
	}

	public void seal () {
		this.body.setLength(0);
		this.body.append("Hi, " + this.list.getLast().getUserName() + " your friends are active!");
		this.body.append("\n");
		for (final Notification n : this.list) {
			this.body.append(formatDate(n.getTimeStamp()) + " " + n.getEventString());
			this.body.append("\n");
		}
	}

	public static String formatDate (final long t) {
		return padRight(padRight(day(t) + ",", 8) + " " + time(t), 20);
	}

	public static String fixedLengthString (final String string, final int length) {
		return padRight(string, length);
	}

	public static String padRight (final String s, final int n) {
		return String.format("%1$-" + n + "s", s);
	}

	public static String padLeft (final String s, final int n) {
		return String.format("%1$" + n + "s", s);
	}

	public static String day (final long timestamp) {
		final Date date = new Date(timestamp);
		final SimpleDateFormat format = new SimpleDateFormat("EEEE");
		return format.format(date);
	}

	public static String time (final long timestamp) {
		final Date date = new Date(timestamp);
		final SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		return format.format(date);
	}

	public void send () {
		L.d("Sending e-mail:");
		L.d("      from", this.from);
		L.d("        to", this.to);
		L.d("   subject", this.subject);
		L.d("          ", this.body);
		L.d();
	}

	public static void sendEmail (final String FROM, final String TO, final String SUBJECT, final String BODY) {

		// Construct an object to contain the recipient address.
		final Destination destination = new Destination().withToAddresses(new String[] {TO});

		// Create the subject and body of the message.
		final Content subject = new Content().withData(SUBJECT);
		final Content textBody = new Content().withData(BODY);
		final Body body = new Body().withText(textBody);

		// Create a message with the specified subject and body.
		final Message message = new Message().withSubject(subject).withBody(body);

		// Assemble the email.
		final SendEmailRequest request = new SendEmailRequest().withSource(FROM).withDestination(destination).withMessage(message);

		try {
			System.out.println("Attempting to send an email through Amazon SES by using the AWS SDK for Java...");

			// Instantiate an Amazon SES client, which will make the service call. The service call requires your AWS credentials.
			// Because we're not providing an argument when instantiating the client, the SDK will attempt to find your AWS
			// credentials
			// using the default credential provider chain. The first place the chain looks for the credentials is in environment
			// variables
			// AWS_ACCESS_KEY_ID and AWS_SECRET_KEY.
			// For more information, see http://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/credentials.html
			final AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.defaultClient();

			// Choose the AWS region of the Amazon SES endpoint you want to connect to. Note that your sandbox
			// status, sending limits, and Amazon SES identity-related settings are specific to a given AWS
			// region, so be sure to select an AWS region in which you set up Amazon SES. Here, we are using
			// the US West (Oregon) region. Examples of other regions that Amazon SES supports are US_EAST_1
			// and EU_WEST_1. For a complete list, see http://docs.aws.amazon.com/ses/latest/DeveloperGuide/regions.html
			final Region REGION = Region.getRegion(Regions.US_WEST_2);
			client.setRegion(REGION);

			// Send the email.
			client.sendEmail(request);
			System.out.println("Email sent!");
		} catch (final Exception ex) {
			System.out.println("The email was not sent.");
			System.out.println("Error message: " + ex.getMessage());
		}
	}

}
