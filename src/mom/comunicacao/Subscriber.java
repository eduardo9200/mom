package mom.comunicacao;

import javax.jms.*;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class Subscriber {

	/*
     * URL do servidor JMS. DEFAULT_BROKER_URL indica que o servidor est· em localhost
     */
    private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
    private static String topicName;
    
    public Subscriber(String topicName) {
        this.topicName = topicName;
       
        //this.execute();
    }
    
    public void execute() {

        try {
            /*
             * Estabelecendo conex√£o com o Servidor JMS
             */		
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
            Connection connection = connectionFactory.createConnection();
            connection.start();

            /*
             * Criando Session 
             */
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            /*
             * Criando Topic
             */ 
            Destination dest = session.createTopic(this.topicName);

            /*
             * Criando Consumidor
             */
            MessageConsumer subscriber = session.createConsumer(dest);

            /*
             * Setando Listener
             */
            subscriber.setMessageListener((MessageListener) this);

        } catch(JMSException e) {
        	System.out.println("Erro: " + e);
        }
   }   

    public void onMessage(Message message) {
        if(message instanceof TextMessage) {
            try {
                System.out.println( ((TextMessage)message).getText());
            }
            catch(JMSException e) {
                System.out.println("Erro: " + e);
            }
        }
    }  
}
