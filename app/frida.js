Java.perform(function () {
    var LoginActivity = Java.use('com.example.pokemon.LoginActivity');

    LoginActivity.isValidCredentials.implementation = function (username, password) {
        console.log('Overwriting isValidCredentials function');
        return true; // Bypassing the login check, always return true
    };
});
