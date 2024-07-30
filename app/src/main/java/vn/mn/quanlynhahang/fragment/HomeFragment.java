package vn.mn.quanlynhahang.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import vn.mn.quanlynhahang.R;
import vn.mn.quanlynhahang.adapter.HomeAdapter;
import vn.mn.quanlynhahang.model.ItemHome;
import vn.mn.quanlynhahang.view.LoginActivity;
import vn.mn.quanlynhahang.viewmodel.HomeViewModel;
import vn.mn.quanlynhahang.viewmodel.ServiceViewModel;

public class HomeFragment extends Fragment {

    private TextView txtUserDetails;
    private ImageView imgAvatarHome;
    private HomeViewModel homeViewModel;
    private List<ItemHome> itemHomeList = new ArrayList<>();
    private RecyclerView recyclerView;
    private HomeAdapter homeAdapter;
    public static String roleUser;
    private ServiceViewModel serviceViewModel;
    public static String currentUserName;
    public static String userid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtUserDetails = view.findViewById(R.id.txtUserDetails);
        recyclerView = view.findViewById(R.id.rvItemHome);
        imgAvatarHome = view.findViewById(R.id.imgAvatarHome);
        serviceViewModel = new ViewModelProvider(requireActivity()).get(ServiceViewModel.class);
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        homeViewModel.getCurrentUser().observe(getViewLifecycleOwner(), firebaseUser -> {
            if (firebaseUser != null) {
                homeViewModel.getUserData(firebaseUser.getUid()).observe(getViewLifecycleOwner(), user -> {
                    if (user != null) {
                        ScheduleFragment.user = user;
                        roleUser = user.getRole();
                        userid = firebaseUser.getUid();
                        if(roleUser == null) {
                            homeViewModel.signOutUser();
                            Toast.makeText(requireContext(), "Bạn không có quyền truy cập ứng dụng", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(requireContext(), LoginActivity.class));
                            requireActivity().finish();
                        }else{
                            createItemHome(roleUser);
                        }

                        String userDetails = "Xin chào, " + user.getFullname();
                        currentUserName = user.getFullname();
                        txtUserDetails.setText(userDetails);
                        Glide.with(requireContext())
                                .load(user.getAvatarurl())
                                .placeholder(R.drawable.avatar)
                                .error(R.drawable.imageerror)
                                .into(imgAvatarHome);
                    } else {
                        txtUserDetails.setText("......");
                    }
                });
            }
        });
        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void createItemHome(String roleUser) {
        if (!isAdded()) {
            return;
        }
        itemHomeList.clear();
        serviceViewModel.getRole(roleUser).addOnSuccessListener(role -> {
            if (!isAdded()) {
                return;
            }
            if (role != null) {
                List<String> danhSach = role.getDanhSach();
                for (String item : danhSach) {
                    int image;
                    String titleName;
                    Class<? extends Fragment> fragmentClass;
                    switch (item) {
                        case "AccountFragment":
                            image = R.drawable.icon_staff;
                            titleName = "Nhân Viên";
                            fragmentClass = AccountFragment.class;
                            break;
                        case "DishManageFragment":
                            image = R.drawable.icon_dish;
                            titleName = "Món Ăn";
                            fragmentClass = DishManageFragment.class;
                            break;
                        case "ServiceFragment":
                            image = R.drawable.icon_service;
                            titleName = "Chức Vụ";
                            fragmentClass = ServiceFragment.class;
                            break;
                        case "TableManageFragment":
                            image = R.drawable.icon_table;
                            titleName = "Bàn Ăn";
                            fragmentClass = TableManageFragment.class;
                            break;
                        case "ScheduleFragment":
                            image = R.drawable.item_schedule;
                            titleName = "Lịch làm việc";
                            fragmentClass = ScheduleFragment.class;
                            break;
                        case "TimeKeepingFragment":
                            image = R.drawable.item_time;
                            titleName = "Chấm Công";
                            fragmentClass = TimeKeepingFragment.class;
                            break;
                        case "DailyRevenueFragment":
                            image = R.drawable.icon_dailyrevenue;
                            titleName = "Doanh Thu";
                            fragmentClass = DailyRevenueFragment.class;
                            break;
                        case "ChartFragment":
                            image = R.drawable.icon_chart;
                            titleName = "Doanh Số Order";
                            fragmentClass = ChartFragment.class;
                            break;
                        default:
                            continue;
                    }
                    itemHomeList.add(new ItemHome(image, titleName, fragmentClass));
                }
                if (isAdded()) {
                    homeAdapter = new HomeAdapter(requireContext(), itemHomeList);
                    recyclerView.setAdapter(homeAdapter);
                    homeAdapter.notifyDataSetChanged();
                }
            }else {
            }
        }).addOnFailureListener(e -> {
            Log.e("SSSSSSSSSSXXX", "error");
        });
    }
}
