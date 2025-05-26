package com.example.ontimeuvers.service;

import android.os.Build;
import android.util.Log;

import com.example.ontimeuvers.entity.Jurusan;
import com.example.ontimeuvers.entity.User;
import com.example.ontimeuvers.model.DetailUserResponse;
import com.example.ontimeuvers.model.FindDataRequest;
import com.example.ontimeuvers.model.FindDataResponse;
import com.example.ontimeuvers.model.FindMahasiswaRequest;
import com.example.ontimeuvers.model.GetMahasiswaResponse;
import com.example.ontimeuvers.model.UpdatePasswordRequest;
import com.example.ontimeuvers.repository.AdminRepository;
import com.example.ontimeuvers.repository.DataKeterlambatanRepository;
import com.example.ontimeuvers.repository.JurusanRepositoryImpl;
import com.example.ontimeuvers.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class AdminServiceImpl implements AdminService {

    private UserRepository userRepository;

    private DataKeterlambatanRepository dataRepository;

    private AdminRepository adminRepository;

    private JurusanRepositoryImpl jurusanRepository;

    public AdminServiceImpl(UserRepository userRepository, DataKeterlambatanRepository dataRepository, JurusanRepositoryImpl jurusanRepository) {
        this.userRepository = userRepository;
        this.dataRepository = dataRepository;
        this.jurusanRepository = jurusanRepository;
    }

    public AdminServiceImpl(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    public AdminServiceImpl(AdminRepository adminRepository, DataKeterlambatanRepository dataRepository, UserRepository userRepository) {
        this.adminRepository = adminRepository;
        this.dataRepository = dataRepository;
        this.userRepository = userRepository;
    }

    public AdminServiceImpl(UserRepository userRepository, DataKeterlambatanRepository dataRepository) {
        this.userRepository = userRepository;
        this.dataRepository = dataRepository;
    }

    private CompletableFuture<List<GetMahasiswaResponse>> dataUsers = null;

    private CompletableFuture<List<GetMahasiswaResponse>> getAllUsers(){
        return userRepository.findAllUser().thenCompose(allUser ->{
            List<CompletableFuture<GetMahasiswaResponse>> futures =
                    allUser.stream().map(user -> dataRepository.getTotalDataUser(user)
                                    .thenApply(totalData -> new GetMahasiswaResponse(user, totalData)))
                            .collect(Collectors.toList());
            return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .thenApply(v -> futures.stream()
                            .map(CompletableFuture::join)
//                            .sorted(Comparator.comparing(o -> o.getUser().getNim()))
                            .collect(Collectors.toList()));
                }
        );
    }

    private synchronized CompletableFuture<List<GetMahasiswaResponse>> getData(){
        if (dataUsers == null){
            dataUsers = getAllUsers();
        }
        return dataUsers;
    }
    public void clearCache() {
        dataUsers = null;
    }
    @Override
    public CompletableFuture<List<GetMahasiswaResponse>> searchMahasiswa(FindMahasiswaRequest request) {
        return getData().thenApply(allUser -> {
            Stream<GetMahasiswaResponse> stream = allUser.stream();
            if (request.getName() != null) {

                if (request.getName().startsWith("20")) {
                    stream = stream.filter(userResponse -> userResponse.getUser().getNim() != null && userResponse.getUser().getNim() .contains(request.getName()));
                } else {
                    stream = stream.filter(userResponse -> userResponse.getUser().getName()  != null && userResponse.getUser().getName().contains(request.getName()));
                }
            }

            if (request.getJurusan() != null) {
                stream = stream.filter(userResponse -> userResponse.getUser().getJurusan() != null && userResponse.getUser().getJurusan().getNama().equals(request.getJurusan()));
            }

            if (request.getAngkatan() != null) {
                stream = stream.filter(userResponse -> userResponse.getUser().getJurusan() != null && Objects.equals(userResponse.getUser().getJurusan().getAngkatan(), request.getAngkatan()));
            }
            return stream
                    .sorted(Comparator.comparing(o -> o.getUser().getNim()))
                    .collect(Collectors.toList());
        });
    }

    private CompletableFuture<List<FindDataResponse>> getAllDataUser = null;

    private CompletableFuture<List<FindDataResponse>> getAllData(){
        return dataRepository.getAllDataKeterlambatan().thenApply(dataKeterlambatans ->
            dataKeterlambatans.stream().map(data ->
                    new FindDataResponse(data.getUser(), data.getLocalDateTime(), data.getMatkul()))
//                    .sorted(Comparator.comparing(FindDataResponse::getWaktu).reversed())
                    .collect(Collectors.toList())
        );
    }

    private synchronized CompletableFuture<List<FindDataResponse>> getAllDataTerlambat(){
        if (getAllDataUser == null){
            getAllDataUser = getAllData();
        }
        return getAllDataUser;
    }
    public void clearCaches() {
        getAllDataUser = null;
    }

    @Override
    public CompletableFuture<List<FindDataResponse>> searchData(FindDataRequest request) {
        Log.i("Request", request.getAngkatan()+"d");
        return getAllDataTerlambat().thenApply(allData ->{
            Stream<FindDataResponse> stream = allData.stream();

            if (request.getName() != null) {

                if (request.getName().startsWith("20")) {
                    stream = stream.filter(dataResponse -> dataResponse.getUser().getNim() != null && dataResponse.getUser().getNim() .contains(request.getName()));
                } else {
                    stream = stream.filter(dataResponse -> dataResponse.getUser().getName()  != null && dataResponse.getUser().getName().contains(request.getName()));
                }
            }

            if (request.getJurusan() != null){
                stream = stream.filter(dataResponse -> dataResponse.getUser().getJurusan() != null && dataResponse.getUser().getJurusan().getNama().equals(request.getJurusan()));
            }

            if (request.getAngkatan() != null){
                stream = stream.filter(dataResponse -> dataResponse.getUser().getJurusan() != null && dataResponse.getUser().getJurusan().getAngkatan().equals(request.getAngkatan()));
            }

            if (request.getTanggal() != null){
                stream = stream.filter(dataResponse -> dataResponse.getWaktu() != null && dataResponse.getWaktu().toLocalDate().equals(request.getTanggal()));
            }
            return stream.sorted(Comparator.comparing(FindDataResponse::getWaktu).reversed()).collect(Collectors.toList());
        });
    }

    public CompletableFuture<String> updatePassword(UpdatePasswordRequest request) {
        return adminRepository.findAdminToken(request.getToken())
                .thenCompose(admin -> {
                    if (!admin.getPassword().equals(request.getOldPassword())) {
                        Log.w("UserService", "Password tidak sama dengan yang lama");
                        return CompletableFuture.completedFuture("Password tidak sama dengan yang lama");
                    }

                    if (!request.getNewPassword().equals(request.getRetypePassword())) {
                        Log.w("UserService", "Password tidak sama dengan retype password");
                        return CompletableFuture.completedFuture("Password tidak sama dengan retype password");
                    }

                    admin.setPassword(request.getNewPassword());

                    return adminRepository.updateAdmin(admin)
                            .thenApply(updatedUser -> {
                                Log.i("UserService", "Success update password");
                                return "Success";
                            });
                })
                .exceptionally(ex -> {
                    Log.e("UserService", "Gagal update password: " + ex.getMessage());
                    return "Gagal update password";
                });
    }

    private CompletableFuture<List<DetailUserResponse>> dataUserCurrents = null;
    private CompletableFuture<List<DetailUserResponse>> getAllDataUserCurrents(User user){
        return dataRepository.getAllDataByUserCurrent(user).thenApply(dataKeterlambatans ->{
            return dataKeterlambatans.stream()
                    .map(data -> new DetailUserResponse(toDate(data.getWaktu()), toTime(data.getWaktu()), data.getMatkul()))
                    .sorted(Comparator.comparing(DetailUserResponse::getTanggal))
                    .collect(Collectors.toList());
        }).exceptionally(ex -> Collections.emptyList());
    }

    public synchronized CompletableFuture<List<DetailUserResponse>> getDetailUser(User user){
        if (dataUserCurrents == null){
            dataUserCurrents = getAllDataUserCurrents(user);
        }
        return dataUserCurrents;
    }

    public void clearCacheDetail() {
        dataUserCurrents = null;
    }

    private String toDateTime(LocalDateTime waktu){
        return waktu.toString();
    }

    private String toDate(String waktu){
        try {
            Log.i("UserService", "Convert to date");
            return LocalDateTime.parse(waktu).toLocalDate().toString();
        }
        catch (Exception e){
            Log.e("DateParse", "Gagal parsing tanggal: " + waktu, e);
            throw new RuntimeException(e);
        }
    }

    private String toTime(String waktu){
        try {
            Log.i("UserService", "Convert to time");
            LocalDateTime ldt = LocalDateTime.parse(waktu);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

            return ldt.toLocalTime().format(formatter);
        }
        catch (Exception e){
            Log.e("DateParse", "Gagal parsing tanggal: " + waktu, e);
            throw new RuntimeException(e);
        }
    }

    private CompletableFuture<List<Jurusan>> allJurusans = null;

    private CompletableFuture<List<Jurusan>> loadAllJurusansFromRepo() {
        return jurusanRepository.getAllJurusan().exceptionally(ex -> Collections.emptyList());
    }

    @Override
    public synchronized CompletableFuture<List<Jurusan>> allJurusan() {
        if (allJurusans == null) {
            allJurusans = loadAllJurusansFromRepo();
        }
        return allJurusans;
    }

    public synchronized void refreshAllJurusans() {
        allJurusans = null;
    }

}

//baru kepikiran
//    private CompletableFuture<List<GetMahasiswaResponse>> getMahasiswaName(String name) {
//        if (name != null && name.startsWith("20")) {
//            return userRepository.findUserByNimLike(name)
//                    .thenCompose(users -> {
//                        List<CompletableFuture<GetMahasiswaResponse>> futures =
//                                users.stream()
//                                .map(user -> dataRepository.getTotalDataUser(user)
//                                        .thenApply(totalData -> new GetMahasiswaResponse(user, totalData)))
//                                .collect(Collectors.toList());
//
//                        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
//                                .thenApply(v -> futures.stream()
//                                        .map(CompletableFuture::join)
//                                        .collect(Collectors.toList()));
//                    });
//        } else {
//            return userRepository.findUserByName(name)
//                    .thenCompose(users -> {
//                        List<CompletableFuture<GetMahasiswaResponse>> futures =
//                                users.stream()
//                                        .map(user -> dataRepository.getTotalDataUser(user)
//                                                .thenApply(totalData -> new GetMahasiswaResponse(user, totalData)))
//                                        .collect(Collectors.toList());
//
//                        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
//                                .thenApply(v -> futures.stream()
//                                        .map(CompletableFuture::join)
//                                        .collect(Collectors.toList()));
//                    });
//        }
//    }
//
//    private CompletableFuture<List<GetMahasiswaResponse>> getMahasiswaByJurusan(String jurusan){
//        return userRepository.findUserByJurusan(jurusan).thenCompose(users -> {
//            List<CompletableFuture<GetMahasiswaResponse>> futures =
//                    users.stream()
//                            .map(user -> dataRepository.getTotalDataUser(user)
//                                    .thenApply(totalData -> new GetMahasiswaResponse(user, totalData)))
//                            .collect(Collectors.toList());
//
//            return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
//                    .thenApply(v -> futures.stream()
//                            .map(CompletableFuture::join)
//                            .collect(Collectors.toList()));
//        });
//    }
//
//    private CompletableFuture<List<GetMahasiswaResponse>> getMahasiswaByAngkatan(String angkatan){
//        return userRepository.findUserByAngkatan(angkatan).thenCompose(users -> {
//            List<CompletableFuture<GetMahasiswaResponse>> futures =
//                    users.stream()
//                            .map(user -> dataRepository.getTotalDataUser(user)
//                                    .thenApply(totalData -> new GetMahasiswaResponse(user, totalData)))
//                            .collect(Collectors.toList());
//
//            return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
//                    .thenApply(v -> futures.stream()
//                            .map(CompletableFuture::join)
//                            .collect(Collectors.toList()));
//        });
//    }
//
//    @Override
//    public CompletableFuture<List<GetMahasiswaResponse>> searchMahasiswa(FindMahasiswaRequest request){
//        if (request.getName() != null) {
//            return getMahasiswaName(request.getName()).thenApply(mahasiswaByName -> {
//                Stream<GetMahasiswaResponse> stream = mahasiswaByName.stream();
//
//                if (request.getJurusan() != null) {
//                    stream = stream.filter(m -> m.getUser().getJurusanKode().contains(request.getJurusan()));
//                }
//
//                if (request.getAngkatan() != null) {
//                    stream = stream.filter(m -> m.getUser().getJurusanKode().contains(request.getAngkatan()));
//                }
//
//                return stream.collect(Collectors.toList());
//            });
//
//        } else if (request.getName() == null && request.getJurusan() != null) {
//            return getMahasiswaByJurusan(request.getJurusan()).thenApply(mahasiswaByJurusan ->{
//                Stream<GetMahasiswaResponse> stream = mahasiswaByJurusan.stream();
//
//                if (request.getAngkatan() != null){
//                    stream = stream.filter(m -> m.getUser().getJurusanKode().contains(request.getAngkatan()));
//                }
//
//                return stream.collect(Collectors.toList());
//            });
//        } else if (request.getName() == null && request.getJurusan() == null && request.getAngkatan() != null) {
//            return getMahasiswaByAngkatan(request.getAngkatan());
//        } else {
//            return CompletableFuture.completedFuture(Collections.emptyList());
//        }
//    }

//getMahasiswa
//                    CompletableFuture<List<GetMahasiswaResponse>> response = userRepository
//                            .findUserByNimLike(request.getName()).thenApply(users ->
//                            users.parallelStream()
//                                    .map(user -> {
//                                        Long totalData = dataRepository.getTotalDataUser(user).join();
//                                        return new GetMahasiswaResponse(user, totalData);
//                                    }).collect(Collectors.toList())
//                            );


//searchMahasiswa
//if (request.getName() != null) {
//List<GetMahasiswaResponse> mahasiswaByName = getMahasiswaName(request.getName()).get();
//
//            if (request.getJurusan() != null){
//List<GetMahasiswaResponse> mahasiswaByNameAndJurusan = mahasiswaByName.stream()
//        .filter(byName -> byName.getUser().getJurusanKode().contains(request.getJurusan()))
//        .collect(Collectors.toList());
//
//                if (request.getAngkatan() != null){
//List<GetMahasiswaResponse> mahasiswaByNameJurusanAndAngkatan = mahasiswaByNameAndJurusan.stream().filter(byNameJurusan ->
//                byNameJurusan.getUser().getJurusanKode().contains(request.getAngkatan()))
//        .collect(Collectors.toList());
//
//                    return CompletableFuture.completedFuture(mahasiswaByNameJurusanAndAngkatan);
//                }else {
//                        return CompletableFuture.completedFuture(mahasiswaByNameAndJurusan);
//                }
//
//                        }else {
//                        return CompletableFuture.completedFuture(mahasiswaByName);
//            }
//
//                    }else {
//                    return CompletableFuture.completedFuture(Collections.emptyList());
//        }