const users = require("./data/users");

module.exports.handler = async (event) => {

    const id = Number(event.pathParameters.id);

    const body = JSON.parse(event.body);

    const user = users.find(user => user.id === id);

    if (!user) {
        return {
            statusCode: 404,
            body: JSON.stringify({
                message: "Usuario no encontrado"
            })
        };
    }

    user.nombre = body.nombre;
    user.email = body.email;

    return {
        statusCode: 200,
        body: JSON.stringify(user)
    };
};