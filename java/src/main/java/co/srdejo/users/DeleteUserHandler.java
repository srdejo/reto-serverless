package co.srdejo.users;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;

import java.util.Map;

public class DeleteUserHandler implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {

    private static final DynamoDbClient DYNAMO = DynamoDbClient.create();
    private static final String TABLE = System.getenv("USERS_TABLE");

    @Override
    public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent event, Context context) {
        String id = event.getPathParameters().get("id");

        try {
            DYNAMO.deleteItem(DeleteItemRequest.builder()
                    .tableName(TABLE)
                    .key(Map.of("id", AttributeValue.fromS(id)))
                    .conditionExpression("attribute_exists(id)")
                    .build());
            return response(200, "{\"message\":\"Usuario eliminado\",\"id\":\"" + id + "\"}");
        } catch (ConditionalCheckFailedException e) {
            return response(404, "{\"message\":\"Usuario no encontrado\"}");
        }
    }

    private static APIGatewayV2HTTPResponse response(int status, String body) {
        return APIGatewayV2HTTPResponse.builder()
                .withStatusCode(status)
                .withHeaders(Map.of("Content-Type", "application/json"))
                .withBody(body)
                .build();
    }
}
