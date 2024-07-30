package vn.mn.quanlynhahang.repository;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import vn.mn.quanlynhahang.model.User;

public class RegisterUserRepository {

    private FirebaseAuth firebaseAuth;
    private SignUpRepository signUpRepository;

    public RegisterUserRepository() {
        firebaseAuth = FirebaseAuth.getInstance();
        signUpRepository = new SignUpRepository();
    }

    public void createUserWithEmailPasswordAndData(String email, String password, User userData, Callback<Boolean> callback) {
        signUpRepository.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String userId = firebaseAuth.getCurrentUser().getUid();
                signUpRepository.saveUserInfoToFirestore(userData, userId)
                        .addOnCompleteListener(saveTask -> {
                            if (saveTask.isSuccessful()) {
                                callback.onSuccess(true);
                            } else {
                                callback.onError(saveTask.getException());
                            }
                        });
            } else {
                callback.onError(task.getException());
            }
        });
    }

    public interface Callback<T> {
        void onSuccess(T data);
        void onError(Throwable throwable);
    }
}
