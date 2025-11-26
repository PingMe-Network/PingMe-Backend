package me.huynhducphu.PingMe_Backend.service.expense.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.request.miniapp.expense.CreateTransactionRequest;
import me.huynhducphu.PingMe_Backend.dto.request.miniapp.expense.UpdateTransactionRequest;
import me.huynhducphu.PingMe_Backend.dto.response.miniapp.expense.TransactionResponse;
import me.huynhducphu.PingMe_Backend.model.miniapp.ExpenseTransaction;
import me.huynhducphu.PingMe_Backend.model.User;
import me.huynhducphu.PingMe_Backend.repository.ExpenseTransactionRepository;
import me.huynhducphu.PingMe_Backend.service.common.CurrentUserProvider;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseTransactionServiceImpl implements me.huynhducphu.PingMe_Backend.service.expense.ExpenseTransactionService {

    private final ExpenseTransactionRepository txRepo;
    private final CurrentUserProvider currentUserProvider;
    private final ModelMapper modelMapper;

    @Override
    public TransactionResponse createTransaction(CreateTransactionRequest request) {

        User user = currentUserProvider.get();

        ExpenseTransaction tx = new ExpenseTransaction();
        tx.setAmount(request.getAmount());
        tx.setType(request.getType());
        tx.setCategory(request.getCategory());
        tx.setDate(request.getDate());
        tx.setNote(request.getNote());
        tx.setUser(user);

        txRepo.save(tx);

        return modelMapper.map(tx, TransactionResponse.class);
    }

    @Override
    public List<TransactionResponse> getTransactionsInMonth(int month, int year) {

        User user = currentUserProvider.get();

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.plusMonths(1).minusDays(1);

        return txRepo
                .findByUserIdAndDateBetween(user.getId(), start, end)
                .stream()
                .map(tx -> modelMapper.map(tx, TransactionResponse.class))
                .toList();
    }

    @Override
    public Long deleteTransaction(Long id) {
        User user = currentUserProvider.get();

        ExpenseTransaction tx = txRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy giao dịch"));

        if (!tx.getUser().getId().equals(user.getId())) {
            throw new EntityNotFoundException("Không tìm thấy giao dịch");
        }

        txRepo.deleteById(id);
        return id;
    }
    @Override
    public TransactionResponse getTransactionDetail(Long id) {
        User user = currentUserProvider.get();

        ExpenseTransaction tx = txRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy giao dịch"));

        if (!tx.getUser().getId().equals(user.getId())) {
            throw new EntityNotFoundException("Không tìm thấy giao dịch");
        }

        return modelMapper.map(tx, TransactionResponse.class);
    }

    @Override
    public TransactionResponse updateTransaction(Long id, UpdateTransactionRequest request) {
        User user = currentUserProvider.get();

        ExpenseTransaction tx = txRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy giao dịch"));

        if (!tx.getUser().getId().equals(user.getId())) {
            throw new EntityNotFoundException("Không tìm thấy giao dịch");
        }

        tx.setAmount(request.getAmount());
        tx.setType(request.getType());
        tx.setCategory(request.getCategory());
        tx.setNote(request.getNote());
        tx.setDate(request.getDate());
        txRepo.save(tx);
        return modelMapper.map(tx, TransactionResponse.class);
    }
}
