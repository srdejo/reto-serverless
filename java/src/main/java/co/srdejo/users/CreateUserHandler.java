package co.srdejo.users;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.util.Map;
import java.util.UUID;

public class CreateUserHandler implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {

    private static final DynamoDbClient DYNAMO = DynamoDbClient.create();
    private static final String TABLE = System.getenv("USERS_TABLE");
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent event, Context context) {
        try {
            JsonNode body = MAPPER.readTree(event.getBody() == null ? "{}" : event.getBody());
            String id = UUID.randomUUID().toString();
            String nombre = body.path("nombre").asText("");
            String email = body.path("email").asText("");

            DYNAMO.putItem(PutItemRequest.builder()
                    .tableName(TABLE)
                    .item(Map.of(
                            "id", AttributeValue.fromS(id),
                            "nombre", AttributeValue.fromS(nombre),
                            "email", AttributeValue.fromS(email)))
                    .build());

            String json = MAPPER.writeValueAsString(Map.of("id", id, "nombre", nombre, "email", email));
            return response(201, json);
        } catch (Exception e) {
            context.getLogger().log("Error creando usuario: " + e);
            return response(500, "{\"message\":\"Error creando usuario\"}");
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
