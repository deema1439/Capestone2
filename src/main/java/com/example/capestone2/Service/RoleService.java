package com.example.capestone2.Service;

import com.example.capestone2.Model.Game;
import com.example.capestone2.Model.Role;
import com.example.capestone2.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    private final GameRepository gameRepository;
    private final RequestsRepository requestsRepository;
    private final OffersRepository offersRepository;
    private final SquadMembersRepository squadMembersRepository;

    public List<Role>getRoles(){
        return roleRepository.findAll();
    }


    public int addRole(Role role){
        Game existingGame=gameRepository.findGameById(role.getGameId());
            if(existingGame==null){
                return 0;//game not found.
            }

            roleRepository.save(role);
            return 1;

    }


    public int updateRole(Integer id,Role role) {
        Role oldRole = roleRepository.findRoleById(id);
        Game existingGame = gameRepository.findGameById(role.getGameId());
        if (oldRole == null) {
            return 0;//role not found
        }
        if (existingGame == null) {
            return 1;//game Id not found
        }
        if (!oldRole.getGameId().equals(role.getGameId())) {
            if (isRoleInUse(id)) {
                return 2; // role is in use, cannot change its game
            }
        }
            oldRole.setName(role.getName());
            oldRole.setGameId(role.getGameId());
            roleRepository.save(oldRole);
            return 3;

    }

    public int deleteRole(Integer id) {
        Role existingRole = roleRepository.findRoleById(id);
        if (existingRole == null) {
            return 0; // role not found
        }
        if (isRoleInUse(id)) {
            return 1; // role is in use, cannot delete
        }
        roleRepository.delete(existingRole);
        return 2;
    }

       public boolean isRoleInUse(Integer roleId) {
            return requestsRepository.existsByRoleId(roleId)
                    || offersRepository.existsByRoleId(roleId)
                    || squadMembersRepository.existsByRoleId(roleId);
        }







    }

















