package pt.ul.fc.css.tascaeats.grpc;

import java.util.UUID;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import pt.ul.fc.css.tascaeats.dtos.user.CreateAdminDTO;
import pt.ul.fc.css.tascaeats.dtos.user.CreateCourierDTO;
import pt.ul.fc.css.tascaeats.dtos.user.LoginUserDTO;
import pt.ul.fc.css.tascaeats.dtos.user.UserDTO;
import pt.ul.fc.css.tascaeats.entities.User;
import pt.ul.fc.css.tascaeats.services.UserService;

@GrpcService
public class UserGrpcService extends UserServiceGrpc.UserServiceImplBase {

    private final UserService userService;

    public UserGrpcService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void login(LoginRequest request, StreamObserver<LoginResponse> responseObserver) {
        try {
            LoginUserDTO loginDTO = new LoginUserDTO(
                    request.getUsername(),
                    request.getPassword()
            );

            UserDTO user = userService.login(loginDTO);

            LoginResponse response = LoginResponse.newBuilder()
                    .setUserId(user.id().toString())
                    .setName(user.name())
                    .setUsername(user.username())
                    .setRole(user.role().toString())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void createAdmin(CreateAdminRequest request, StreamObserver<UserResponse> responseObserver) {
        try {
            CreateAdminDTO dto = new CreateAdminDTO(
                    request.getName(),
                    request.getUsername(),
                    request.getPassword()
            );

            UserDTO user = new UserDTO(userService.registerAdmin(dto));

            responseObserver.onNext(toGrpcUserDTOResponse(user));
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void createCourier(CreateCourierRequest request, StreamObserver<UserResponse> responseObserver) {
        try {
            CreateCourierDTO dto = new CreateCourierDTO(
                    request.getName(),
                    request.getUsername(),
                    request.getPassword()
            );

            UserDTO user = new UserDTO(userService.registerCourier(dto));

            responseObserver.onNext(toGrpcUserDTOResponse(user));
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void removeUser(UserIdRequest request, StreamObserver<BooleanResponse> responseObserver) {
        try {
            userService.removeUser(UUID.fromString(request.getUserId()));

            BooleanResponse response = BooleanResponse.newBuilder()
                    .setValue(true)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void getUsersByFilter(UserFilterRequest request, StreamObserver<UserListResponse> responseObserver) {
        try {
            var users = userService.getAllUsers();

            UserListResponse.Builder response = UserListResponse.newBuilder();

            for (var user : users) {
                response.addUsers(toGrpcUserResponse(user));
            }

            responseObserver.onNext(response.build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    private UserResponse toGrpcUserResponse(User user) {
        return UserResponse.newBuilder()
                .setId(user.getId().toString())
                .setName(user.getName())
                .setUsername(user.getUsername())
                .setRole(user.getRole().toString())
                .build();
    }

    private UserResponse toGrpcUserDTOResponse(UserDTO user) {
        return UserResponse.newBuilder()
                .setId(user.id().toString())
                .setName(user.name())
                .setUsername(user.username())
                .setRole(user.role().toString())
                .build();
    }
}