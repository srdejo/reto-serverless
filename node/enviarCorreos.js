const { SNSClient, PublishCommand } = require("@aws-sdk/client-sns");

// El SDK v3 ya viene incluido en el runtime nodejs20.x de Lambda
const sns = new SNSClient({});
const TOPIC_ARN = process.env.TOPIC_ARN;

module.exports.handler = async (event) => {
    for (const record of event.Records) {
        const user = JSON.parse(record.body);

        await sns.send(new PublishCommand({
            TopicArn: TOPIC_ARN,
            Subject: "Nuevo usuario creado",
            Message: `Se creó el usuario ${user.nombre} (${user.email}) con id ${user.id}.`
        }));
    }
};
