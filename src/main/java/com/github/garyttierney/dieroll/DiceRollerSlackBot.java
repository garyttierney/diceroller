package com.github.garyttierney.dieroll;

import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.SlackUser;
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory;

import java.security.SecureRandom;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DiceRollerSlackBot {

    public static void main(String[] argv) throws Exception {
        final String slackToken = System.getProperty("SLACK_TOKEN");

        if (slackToken == null) {
            throw new RuntimeException("No SLACK_TOKEN system property set");
        }

        final SlackSession mySession = SlackSessionFactory.createWebSocketSlackSession(slackToken);
        final Pattern regex = Pattern.compile("(?<quantity>\\d+)?d(?<faces>\\d+)(?<modifierOp>[\\+\\-])?(?<modifier>\\d+)?");
        final SecureRandom rng = new SecureRandom();
        final DiceRoller roller = new DiceRoller(rng);

        mySession.addMessagePostedListener((event, session) -> {
            int rollCounter = 0;

            final SlackUser botUser = session.findUserByUserName("d20bot");
            final SlackChannel chan = event.getChannel();
            final SlackUser user = event.getSender();

            final String message = event.getMessageContent();
            final Matcher matcher = regex.matcher(message);

            if (!message.contains(botUser.getId())) {
                return;
            }

            while (matcher.find()) {
                final int faces = Integer.parseInt(matcher.group("faces"));
                final int quantity = Optional.ofNullable(matcher.group("quantity")).map(Integer::parseInt).orElse(1);
                final int modifier = Optional.ofNullable(matcher.group("modifier")).map(Integer::parseInt).orElse(0);
                final char modifierOp = Optional.ofNullable(matcher.group("modifierOp")).map(val -> val.charAt(0)).orElse('+');
                final String roll = roller.roll(faces, quantity, modifierOp == '+' ? modifier : -modifier);

                session.sendMessage(chan, String.format("%s rolled a %s on roll #%d", user.getUserName(), roll, ++rollCounter));
            }
        });

        mySession.connect();
    }
}
