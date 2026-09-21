const { UpdateCommand } = require("@aws-sdk/lib-dynamodb");
const { client, TABLE } = require("./db");

module.exports.handler = async (event) => {

    const id = event.pathParameters.id;

    const body = JSON.parse(event.body);

    try {
        const result = await client.send(new UpdateCommand({
            TableName: TABLE,
            Key: { id },
            UpdateExpression: "SET nombre = :nombre, email = :email",
            ConditionExpression: "attribute_exists(id)",
            ExpressionAttributeValues: {
                ":nombre": body.nombre,
                ":email": body.email
            },
            ReturnValues: "ALL_NEW"
        }));

        return {
            statusCode: 200,
            body: JSON.stringify(result.Attributes)
        };
    } catch (error) {
        if (error.name === "ConditionalCheckFailedException") {
            return {
                statusCode: 404,
                body: JSON.stringify({
                    message: "Usuario no encontrado"
                })
            };
        }
        throw error;
    }
};
