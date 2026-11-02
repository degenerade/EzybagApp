package bag.ezy.ezybagapp.model.queries

object GoogleAuthHandler {
    var onGoogleSignInRequested: suspend () -> Result<Pair<String, String>> = {
        Result.failure(Exception("Google Sign-In callback not registered in MainActivity"))
    }
}
