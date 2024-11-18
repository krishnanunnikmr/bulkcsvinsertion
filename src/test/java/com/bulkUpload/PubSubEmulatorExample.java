import com.google.cloud.pubsub.v1.SubscriptionAdminClient;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.ProjectTopicName;
import com.google.pubsub.v1.PushConfig;

public class PubSubEmulatorExample {

    private static final String PROJECT_ID = "test-project"; // Use a dummy project ID
    private static final String PUBSUB_EMULATOR_HOST = "localhost:8085"; // Emulator host

    public static void main(String[] args) {
        // Set the PUBSUB_EMULATOR_HOST environment variable
        System.setProperty("PUBSUB_EMULATOR_HOST", PUBSUB_EMULATOR_HOST);

        // Topic and subscription names
        String topicName = "my-test-topic";
        String subscriptionName = "my-test-subscription";

        // Create the topic
        createTopic(topicName);

        // Create the subscription
        createSubscription(topicName, subscriptionName);
    }

    /**
     * Creates a Pub/Sub topic in the emulator.
     *
     * @param topicName the name of the topic to create
     */
    public static void createTopic(String topicName) {
        try (TopicAdminClient topicAdminClient = TopicAdminClient.create()) {
            ProjectTopicName topic = ProjectTopicName.of(PROJECT_ID, topicName);
            topicAdminClient.createTopic(topic);
            System.out.println("Created topic: " + topicName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a Pub/Sub subscription in the emulator.
     *
     * @param topicName        the name of the topic to subscribe to
     * @param subscriptionName the name of the subscription to create
     */
    public static void createSubscription(String topicName, String subscriptionName) {
        try (SubscriptionAdminClient subscriptionAdminClient = SubscriptionAdminClient.create()) {
            ProjectSubscriptionName subscription = ProjectSubscriptionName.of(PROJECT_ID, subscriptionName);
            ProjectTopicName topic = ProjectTopicName.of(PROJECT_ID, topicName);

            // Create the subscription
            subscriptionAdminClient.createSubscription(
                subscription, 
                topic, 
                PushConfig.getDefaultInstance(), 
                0
            );
            System.out.println("Created subscription: " + subscriptionName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
