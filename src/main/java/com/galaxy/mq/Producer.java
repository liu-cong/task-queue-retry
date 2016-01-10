package com.galaxy.mq;
import java.util.Map;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Producer {

	@Autowired
	private AmqpTemplate amqpTemplate;

	/**Encapsulate the data and header into a message and then
	 * publish the message to the specified exchange with routing key
	 * @param exchange the exchange that the message is being published to
	 * @param routingKey the routing key of this message
	 * @param header the header of the message 
	 * @param data the data to be encapsulated to the message
	 * @return void
	 * */
	public void publish(String exchange, String routingKey, final Map<String, Object> header, Object data) {
		System.out.println("[Producer] publish task...Exchange:"+exchange+" RoutingKey:"+routingKey
				+ " Header:" + header + " Data:" +data.toString());
		amqpTemplate.convertAndSend(exchange,routingKey,data, new MessagePostProcessor() {
			/*Add header to the message*/
			@Override
			public Message postProcessMessage(Message message) throws AmqpException {
				for (Map.Entry<String, Object> entry: header.entrySet()){
					message.getMessageProperties().setHeader(entry.getKey(), entry.getValue());
				}
				return message;
			}
		});
	}

	/**Publish the message to the specified exchange with routing key
	 * @param exchange the exchange that the message is being published to
	 * @param routingKey the routing key of this message
	 * @param message the message to be published
	 * @return void
	 * */
	public void publish(String exchange, String routingKey, Message message) {
		System.out.println("[Producer] publish task...Exchange:"+exchange+" RoutingKey:"+routingKey
				+ " Header:" + message.getMessageProperties().getHeaders());
		amqpTemplate.send(exchange, routingKey, message);
	}
}
