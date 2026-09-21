const { ScanCommand } = require("@aws-sdk/lib-dynamodb");
const { client, TABLE } = require("./db");

module.exports.handler = async () => {
    const result = await client.send(new ScanCommand({ TableName: TABLE }));

    return {
        statusCode: 200,
        body: JSON.stringify(result.Items)
    };
};
