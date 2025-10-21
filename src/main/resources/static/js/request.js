document.addEventListener('DOMContentLoaded', function() {
    const dateInput = document.getElementById('date');
    if(dateInput) {
        const today = new Date();
        const yyyy = today.getFullYear();
        const mm = String(today.getMonth() + 1).padStart(2, '0');
        const dd = String(today.getDate()).padStart(2, '0');
        const todayString = `${yyyy}-${mm}-${dd}`;
        dateInput.setAttribute('min', todayString);
    }

    const pcSelect = document.getElementById('pcId');
    const periodCheckboxes = document.querySelectorAll('input[name="periodIds"]');

    const localBookedData = (typeof bookedData !== 'undefined') ? bookedData : {};

    if (pcSelect) {
        function updatePeriodCheckboxes() {
            const selectedPcId = pcSelect.value;
            const bookedPeriods = localBookedData[selectedPcId] || [];

            periodCheckboxes.forEach(function(checkbox) {
                const periodValue = parseInt(checkbox.value, 10);

                if (bookedPeriods.includes(periodValue)) {
                    checkbox.disabled = true;
                    checkbox.checked = false;
                    checkbox.parentElement.classList.add('text-muted');
                } else {
                    checkbox.disabled = false;
                    checkbox.parentElement.classList.remove('text-muted');
                }
            });
        }

        pcSelect.addEventListener('change', updatePeriodCheckboxes);

        updatePeriodCheckboxes();
    }


    const selectAmBtn = document.getElementById('select-am');
    const selectPmBtn = document.getElementById('select-pm');
    const selectAllDayBtn = document.getElementById('select-all-day');
    const selectClearBtn = document.getElementById('select-clear');

    if (selectAmBtn) {
        selectAmBtn.addEventListener('click', function() {
            periodCheckboxes.forEach(function(checkbox) {
                const periodValue = parseInt(checkbox.value, 10);
                if (!checkbox.disabled) {
                    checkbox.checked = periodValue <= 2;
                }
            });
        });
    }

    if (selectPmBtn) {
        selectPmBtn.addEventListener('click', function() {
            periodCheckboxes.forEach(function(checkbox) {
                const periodValue = parseInt(checkbox.value, 10);
                if (!checkbox.disabled) {
                    checkbox.checked = periodValue > 2;
                }
            });
        });
    }

    if (selectAllDayBtn) {
        selectAllDayBtn.addEventListener('click', function() {
            periodCheckboxes.forEach(function(checkbox) {
                if (!checkbox.disabled) {
                    checkbox.checked = true;
                }
            });
        });
    }

    if (selectClearBtn) {
        selectClearBtn.addEventListener('click', function() {
            periodCheckboxes.forEach(function(checkbox) {
                checkbox.checked = false;
            });
        });
    }
});
