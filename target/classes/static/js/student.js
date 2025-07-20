$(document).ready(function() {
    // Initialize Bootstrap tooltips
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
    
    // Delete confirmation dialog
    $('.delete-form').on('submit', function(e) {
        if (!confirm('정말로 이 학생을 삭제하시겠습니까?\n이 작업은 되돌릴 수 없습니다.')) {
            e.preventDefault();
            return false;
        }
    });
    
    // Client-side form validation
    $('#studentForm').on('submit', function(e) {
        var isValid = true;
        var name = $('#name').val().trim();
        var email = $('#email').val().trim();
        
        // Clear previous error states
        $('.form-control').removeClass('is-invalid');
        $('.invalid-feedback').remove();
        
        // Validate name
        if (name === '') {
            $('#name').addClass('is-invalid');
            $('#name').after('<div class="invalid-feedback">이름을 입력해주세요.</div>');
            isValid = false;
        }
        
        // Validate email
        if (email === '') {
            $('#email').addClass('is-invalid');
            $('#email').after('<div class="invalid-feedback">이메일을 입력해주세요.</div>');
            isValid = false;
        } else {
            var emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!emailRegex.test(email)) {
                $('#email').addClass('is-invalid');
                $('#email').after('<div class="invalid-feedback">올바른 이메일 형식이 아닙니다.</div>');
                isValid = false;
            }
        }
        
        if (!isValid) {
            e.preventDefault();
            return false;
        }
    });
    
    // Add Bootstrap validation styles on blur
    $('.form-control').on('blur', function() {
        var $this = $(this);
        var value = $this.val().trim();
        
        if ($this.attr('required')) {
            if (value === '') {
                $this.addClass('is-invalid').removeClass('is-valid');
            } else {
                if ($this.attr('type') === 'email') {
                    var emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
                    if (emailRegex.test(value)) {
                        $this.addClass('is-valid').removeClass('is-invalid');
                    } else {
                        $this.addClass('is-invalid').removeClass('is-valid');
                    }
                } else {
                    $this.addClass('is-valid').removeClass('is-invalid');
                }
            }
        }
    });
    
    // Clear validation states on focus
    $('.form-control').on('focus', function() {
        $(this).removeClass('is-invalid is-valid');
    });
    
    // Auto-hide alert messages after 5 seconds
    $('.alert').delay(5000).fadeOut('slow');
    
    // Search form enhancement
    $('#searchForm').on('submit', function(e) {
        var searchValue = $('input[name="name"]').val().trim();
        if (searchValue === '') {
            e.preventDefault();
            alert('검색어를 입력해주세요.');
            $('input[name="name"]').focus();
            return false;
        }
    });
});